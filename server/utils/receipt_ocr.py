__author__ = 'michal'

import subprocess
import sys
import re
import Levenshtein
import psycopg2

NIP_REGEXP = re.compile('.*NIP.(\d{3}-\d{2}-\d{2}-\d{3}).*')
PRODUCT_REGEXP = re.compile('(.*) (.)\*(.+,.+)= (.+,.+) .')
PARAGON_THRESHOLD = 0.7
PARAGON_TEXT = "PARAGON FISKALNY"
PRODUCT_NAME_THRESHOLD = 0.4

PRODUCT_MATCH_SQL = "SELECT * FROM " \
                    "(SELECT id, name, levenshtein(\"name\", '{0}') / greatest(char_length(\"name\"), char_length('{0}'))::float as ratio from products) r " \
                    "WHERE ratio < %f ORDER BY ratio" % (PRODUCT_NAME_THRESHOLD, )

def connect_to_db():
    try:
        return psycopg2.connect("dbname='aghacks' user='michal' host='localhost' password='michal'")
    except:
        print("DB connection failed")

def parse_price(t):
    t = t.replace('T', '1')
    t = t.replace(',','.')
    return float(t)

def match_product_name(t, conn):
    cur = conn.cursor()
    sql = PRODUCT_MATCH_SQL.format(t)
    cur.execute(sql)

    rows = cur.fetchall()
    if len(rows) == 0:
        return None, t
    else:
        return rows[0][0], rows[0][1]


def product_parse(line, conn):
    m = PRODUCT_REGEXP.match(line)
    name, quantity, unit_price, price = m.groups()

    id, name = match_product_name(name, conn)
    quantity = parse_price(quantity)
    unit_price = parse_price(unit_price)
    price = parse_price(price)

    return {'id': id, 'name': name, 'quantity': quantity, 'unit_price': unit_price, 'price': price}

def do_ocr(filename, conn):
    ocr_text = subprocess.check_output(["tesseract", filename, "-l", "pol", "-"]).decode()

    lines = ocr_text.split('\n')

    nip = None
    products_start = False
    products = []

    for l in lines:

        if nip is None:
            m = NIP_REGEXP.match(l)
            if m:
                nip = m.group(1)
                continue

        if not products_start:
            if Levenshtein.ratio(l, PARAGON_TEXT) >= PARAGON_THRESHOLD:
                products_start = True
        else:
            if l.strip() == '':
                break

            products.append(product_parse(l, conn))

    return {'nip': nip, 'products': products}

if __name__ == '__main__':

    if len(sys.argv) != 2:
        print("%s [filename]" % (sys.argv[0]))
        sys.exit(1)

    filename = sys.argv[1]

    conn = connect_to_db()
    receipt = do_ocr(filename, conn)
    conn.close()

    print(receipt)