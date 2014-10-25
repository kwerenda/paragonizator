__author__ = 'michal'

import subprocess
import sys
import re
import Levenshtein
import psycopg2
import threading
from pprint import pprint

class ReceiptOcr:

    NIP_REGEXP = re.compile('.*NIP.(.{3}.\d{2}.\d{2}.\d{3}).*')
    PRODUCT_REGEXP = re.compile('(.*) (.+)\*(.+,.+)= (.+,.+) .')
    PARAGON_THRESHOLD = 0.7
    PARAGON_TEXT = "PARAGON FISKALNY"
    PRODUCT_NAME_THRESHOLD = 0.4

    IM_CONVERT_CMD = "convert -threshold {0}% -colorspace Gray -depth 1 -density 300 {1} -"

    PRODUCT_MATCH_SQL = "SELECT * FROM " \
                        "(SELECT id, name, levenshtein(\"name\", %(name)s) / greatest(char_length(\"name\"), char_length(%(name)s))::float as ratio from products) r " \
                        "WHERE ratio < {0} ORDER BY ratio".format(PRODUCT_NAME_THRESHOLD)

    def __init__(self, filename):
        self.filename = filename
        self.bw_filename = filename + ".bw.jpg"
        self.conn = self.connect_to_db()
        self.receipts = {}

    def close(self):
        self.conn.close()

    def connect_to_db(self):
        try:
            return psycopg2.connect("dbname='aghacks' user='michal' host='localhost' password='michal'")
        except:
            print("DB connection failed")

    def parse_price(self, t):
        t = t.replace('T', '1')
        t = t.replace(',','.')
        try:
            return float(t)
        except ValueError:
            return None


    def match_product_name(self, t):
        cur = self.conn.cursor()
        cur.execute(self.PRODUCT_MATCH_SQL, {'name': t})

        rows = cur.fetchall()
        if len(rows) == 0:
            return None, t
        else:
            return rows[0][0], rows[0][1]


    def product_parse(self, line):
        m = self.PRODUCT_REGEXP.match(line)
        if m is None or len(m.groups()) == 0:
            return

        name, quantity, unit_price, price = m.groups()

        id, name = self.match_product_name(name)
        quantity = self.parse_price(quantity)
        unit_price = self.parse_price(unit_price)
        price = self.parse_price(price)

        if quantity is None and unit_price is not None and price is not None:
            quantity = round(price / unit_price, 2)

        if unit_price is None and quantity is not None and price is not None:
            unit_price = round(price / quantity, 2)

        if price is None and unit_price is not None and quantity is not None:
            price = unit_price * quantity

        return {'id': id, 'name': name, 'quantity': quantity, 'unit_price': unit_price, 'price': price}

    def do_ocr(self, threshold):

        print("doing ocr for threshold " + str(threshold))

        convert_cmd = self.IM_CONVERT_CMD.format(threshold, self.filename)
        convert_sp = subprocess.Popen(convert_cmd.split(" "),
                                   stdout=subprocess.PIPE)

        ocr_sp = subprocess.Popen(["tesseract", "-l", "pol", "-", "-"],
                                  stdin = convert_sp.stdout,
                                  stdout = subprocess.PIPE)
        ocr_sp.wait()

        out = ocr_sp.communicate()[0].decode()
        lines = out.split('\n')

        nip = None
        products_start = False
        products = []

        for l in lines:

            if nip is None:
                m = self.NIP_REGEXP.match(l)
                if m:
                    nip = m.group(1)
                    continue

            if not products_start:
                if Levenshtein.ratio(l, self.PARAGON_TEXT) >= self.PARAGON_THRESHOLD:
                    products_start = True
            else:
                if l.strip() == '':
                    continue

                parsed_product = self.product_parse(l)
                if parsed_product is not None:
                    products.append(parsed_product)

        self.receipts[threshold] = {'nip': nip, 'products': products}

    def obtain_receipt(self):
        threads = []
        for threshold in range(5, 25, 2):
            thread = threading.Thread(target=self.do_ocr,
                                      args = (threshold, ))
            threads.append(thread)
            thread.start()

        for thread in threads:
            thread.join()

        best_products = None
        nip = None
        for threshold, receipt in self.receipts.items():
            if best_products is None:
                best_products = receipt['products']
            else:
                if len(receipt['products']) > len(best_products):
                    best_products = receipt['products']

            if nip is None and receipt['nip'] is not None:
                nip = receipt['nip']

        return {'shop': {'nip': nip}, 'products' : best_products}


if __name__ == '__main__':

    if len(sys.argv) != 2:
        print("%s [filename]" % (sys.argv[0]))
        sys.exit(1)

    filename = sys.argv[1]

    receipt_ocr = ReceiptOcr(filename)
    pprint(receipt_ocr.obtain_receipt())
    receipt_ocr.close()

