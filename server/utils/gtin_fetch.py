__author__ = 'michal'

import requests
from bs4 import BeautifulSoup

class GtinFetch:

    URL="http://www.produktywsieci.pl/gtin/"

    def __init__(self):
        pass

    def fetch_product_name(self, gtin):
        r = requests.get(self.URL + str(gtin))
        soup = BeautifulSoup(r.text)
        res = soup.find_all("div", { "class" : "product-information-basic" })
        if len(res) == 0:
            return None

        product_name = res[0].find_next("p").contents[0]
        return product_name

if __name__ == '__main__':
    gtin_fetch = GtinFetch()
    print(gtin_fetch.fetch_product_name("05902078000201"))