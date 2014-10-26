import json
import os
import os.path
import uuid

import requests
from bs4 import BeautifulSoup
from flask import request
from flask.ext.restful import Resource, reqparse
from server.database import models
from server.database.database import db
from server.database.models import Product, Shop, PriceEntry, User, Receipt, ProductAlias
from server.utils.receipt_ocr import ReceiptOcr
from server.utils.gtin_fetch import GtinFetch
from sqlalchemy.sql import text
from datetime import datetime

class DataGen(Resource):


    def put(self):
        print("Generating data")

        self.gen_data()

    def gen_data(self):

        db.get_db().engine.execution_options(autocommit=True).execute("TRUNCATE shops, products, product_aliases_on_recepits, receipts, price_entries, users CASCADE;")

        user = User("m4jkel@gmail.com", "SRID=4326;POINT(50.094571 19.884915)")
        db.add(user)

        shops = []

        shop = Shop("Biedronka", "SRID=4326;POINT(50.074571 19.894915)", "123")
        db.add(shop)
        shops.append(shop)

        shop = Shop("Lidl", "SRID=4326;POINT(50.094571 19.824915)", "1232")
        db.add(shop)
        shops.append(shop)

        shop = Shop("Carrefour", "SRID=4326;POINT(50.174571 19.494915)", "323")
        db.add(shop)
        shops.append(shop)

        shop = Shop("Carrefour 2", "SRID=4326;POINT(50.374571 19.994915)", "1235")
        db.add(shop)
        shops.append(shop)

        shop = Shop("Delikatesy", "SRID=4326;POINT(50.574571 19.894915)", "1535")
        db.add(shop)
        shops.append(shop)

        products = []

        product = Product(1, "Napój Tiger")
        db.add(product)
        products.append(product)

        product = Product(2, "Masło Polskie")
        db.add(product)
        products.append(product)

        product = Product(3, "Woda nałęczowianka")
        db.add(product)
        products.append(product)

        product = Product(8, "Kawa Jackobs")
        db.add(product)
        products.append(product)

        receipts = []
        for shop in shops:
            receipt = Receipt(user.email, shop.id, datetime.now())
            receipts.append(receipt)
            db.add(receipt)

        for product in products:
            alias = product.name.replace(' ', '')[0:5]
            for shop in shops:
                product_alias = ProductAlias(alias, product.gtin, shop.id)
                db.add(product_alias)


