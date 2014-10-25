__author__ = 'michal'

import subprocess
import sys
import re
import Levenshtein
import psycopg2

class ReceiptOcr:

    NIP_REGEXP = re.compile('.*NIP.(\d{3}-\d{2}-\d{2}-\d{3}).*')
    PRODUCT_REGEXP = re.compile('(.*) (.)\*(.+,.+)= (.+,.+) .')
    PARAGON_THRESHOLD = 0.7
    PARAGON_TEXT = "PARAGON FISKALNY"
    PRODUCT_NAME_THRESHOLD = 0.4

    PRODUCT_MATCH_SQL = "SELECT * FROM " \
                        "(SELECT id, name, levenshtein(\"name\", '{0}') / greatest(char_length(\"name\"), char_length('{0}'))::float as ratio from products) r " \
                        "WHERE ratio < %f ORDER BY ratio" % (PRODUCT_NAME_THRESHOLD, )

    def __init__(self, filename):
        self.filename = filename
        self.conn = self.connect_to_db()


    def connect_to_db(self):
        try:
            return psycopg2.connect("dbname='aghacks' user='michal' host='localhost' password='michal'")
        except:
            print("DB connection failed")

    def parse_price(self, t):
        t = t.replace('T', '1')
        t = t.replace(',','.')
        return float(t)

    def match_product_name(self, t):
        cur = self.conn.cursor()
        sql = self.PRODUCT_MATCH_SQL.format(t)
        cur.execute(sql)

        rows = cur.fetchall()
        if len(rows) == 0:
            return None, t
        else:
            return rows[0][0], rows[0][1]


    def product_parse(self, line):
        m = self.PRODUCT_REGEXP.match(line)
        name, quantity, unit_price, price = m.groups()

        id, name = self.match_product_name(name)
        quantity = self.parse_price(quantity)
        unit_price = self.parse_price(unit_price)
        price = self.parse_price(price)

        return {'id': id, 'name': name, 'quantity': quantity, 'unit_price': unit_price, 'price': price}

    def do_ocr(self):
        ocr_text = subprocess.check_output(["tesseract", self.filename, "-l", "pol", "-"]).decode()

        lines = ocr_text.split('\n')

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
                    break

                products.append(self.product_parse(l))

        return {'shop': {'nip': nip}, 'products': products}

if __name__ == '__main__':

    if len(sys.argv) != 2:
        print("%s [filename]" % (sys.argv[0]))
        sys.exit(1)

    filename = sys.argv[1]

    receipt_ocr = ReceiptOcr(filename)
    receipt = receipt_ocr.do_ocr()

    print(receipt)