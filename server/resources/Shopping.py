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
from server.database.models import Product, Shop, PriceEntry
from server.utils.receipt_ocr import ReceiptOcr
from server.utils.gtin_fetch import GtinFetch
from server.utils.shopping_optimizer import ShoppingOptimizer
from sqlalchemy.sql import text
from geoalchemy2 import Geography

class User(Resource):

    def put(self):
        """
        insert new user to database

        :return:
        """
        print("Adding new user")
        parser = reqparse.RequestParser()
        parser.add_argument('email', type=str, location='args', required=True)
        args = parser.parse_args(request)

        if models.User.query.filter_by(email = args['email']).count() == 0:
            new_user = models.User(args['email'])
            db.add(new_user)
            return {"message": "User added"}

    def post(self):
        """
        update user's location in database
        :return:
        """
        print("Updating location")
        parser = reqparse.RequestParser()
        parser.add_argument('email', type=str, location='args', required=True)
        parser.add_argument('loc_lat', type=str, location='args')
        parser.add_argument('loc_long', type=str, location='args')
        args = parser.parse_args(request)
        if 'loc_lat' in args and 'loc_long' in args:
            usr = models.User.query.filter_by(email=args['email']).first()
            print('Updating user {0} location'.format(args['email']))
            if usr:
                usr.last_location = 'SRID=4326;POINT({0} {1})'.format(args['loc_lat'], args['loc_long'])
                db.commit()
                return {"message": "User location updates"}
            return {"message": "User not found"}
        return {"message": "Location information not found"}


class ShoppingList(Resource):

    def put(self):
        """
        optimizing provided shopping list
        :return: optimal shopping list
        """



        #
        # sql = text("select name, ST_X(location) as latitude, ST_Y(location) as longitude from shops "
        #            "where ST_DWithin(Geography(location), "
        #            "(SELECT Geography(users.last_location) from users where email = :email), :radius)")
        #
        # rows = db.get_db().engine.execute(sql, email=email, radius=radius)
        #
        # shops = []
        # for row in rows:
        #     shops.append({'name': row['name'], 'latitude': row['latitude'], 'longitude': row['longitude']})
        #
        # return shops

    def post(self):

        req = request.get_json()

        email = req['email']
        radius = req['radius']
        shops_limit = req['shopsLimit']
        products = req['itemNames']

        print("Loading shopping list: email=%s, radius=%i, shops_limit=%i, products=%s" % (email, radius, shops_limit, str(products)))

        so = ShoppingOptimizer()
        return so.findOptimalShopSet(email, radius, shops_limit, products)


class Gtin(Resource):
    def put(self):
        """
        Loading barcode to database (barcode and receipt_name as query arguments)
        :return:
        """
        print("Reading barcode arguments")
        parser = reqparse.RequestParser()
        parser.add_argument('gtin', type=int, help='Barcode cannot be converted', location='args')
        args = parser.parse_args(request)
        gtin = args["gtin"]
        prod = models.Product.query.filter_by(gtin=int(gtin)).first()
        if not prod:
            gtin_fetch = GtinFetch()
            product_name = gtin_fetch.fetch_product_name(gtin)
            new_product = Product(int(gtin), product_name)
            db.add(new_product)
            print("GTIN added")
        else:
            print("GTIN exists")
            product_name = prod.name
        return {'message': "GTIN added", "product": product_name}

class Receipt(Resource):

    def put(self):
        """
        Fix receipt mistakes, needs user_id (email) as query parameter
        :return: status of fixed receipt
        """

        parser = reqparse.RequestParser()
        parser.add_argument('user_id', type=str, location='args', required=True)
        args = parser.parse_args(request)

        user_id = args['user_id']

        fixed_receipt = json.load(request.get_json())
        products = fixed_receipt["products"]
        shop_name = fixed_receipt["shop"]["name"]
        location = ""
        if fixed_receipt["shop"]["location"]:
            location = "POINT({0} {1})".format(fixed_receipt["shop"]["location"][0], fixed_receipt["shop"]["location"][1])
        nip = fixed_receipt["shop"]["nip"]

        shop = Shop.query.filter_by(nip=nip).first()
        if shop:
            shop.location = location
            shop.name = shop_name
            db.commit()
        else:
            shop = Shop(shop_name, location, nip)
            db.add(shop)

        new_receipt = models.Receipt(user_id, shop.id)
        db.add(new_receipt)

        alch_db = db.get_db()
        for product in products:
            if not product["id"]:
                prod = Product(product["name"])
                db.add(prod)
                product["id"] = prod.id
            price_entry = PriceEntry(product["id"], shop.id, new_receipt.id, product['price'], product['quantity'], product['unit'])
            alch_db.session.add(price_entry)

        alch_db.session.commit()

        return {'message': 'saved'}

class Ocr(Resource):
    UPLOADED_FILES_DIR = "/tmp/receipts"

    def get_company_info(self, nip):
        try:
            url = 'http://www.money.pl/rejestr-firm/nip/{0}'.format(nip)
            response = requests.get(url)
            if response.return_code == 200:
                soup = BeautifulSoup(response.text)
                name_address = [x.strip().encode("latin1", "replace")
                                for x in soup.find("li",{"class":"cb"}).get_text().translate(str.maketrans("", "", "\t\r")).split("\n") if x]
                return name_address[0], " ".join(name_address[1:])
        except:
            pass
        return "", ""

    def post(self):
        """
        Uploads receipt
        :return: ocr'ed receipt to fix mistakes
        """

        print(request.files)
        print(request.data)
        print(request.values)

        uploaded_receipt = request.files['file']

        if not os.path.exists(self.UPLOADED_FILES_DIR):
            os.mkdir(self.UPLOADED_FILES_DIR)

        filepath = os.path.join(self.UPLOADED_FILES_DIR, "%s.jpg" % (uuid.uuid4(), ))

        try:
            f = open(filepath, 'wb')
            f.write(uploaded_receipt.read())
            f.close()
            print("%s file saved" % (filepath, ))
        except IOError as e:
            print(e)

        receipt_ocr = ReceiptOcr(filepath)
        receipt = receipt_ocr.obtain_receipt()
        print(receipt)
        # name, address = self.get_company_info(receipt['shop']['nip'])
        receipt['shop']['name'] = ""
        receipt['shop']['location'] = ""
        if receipt['shop']['nip']:
            available_shop = Shop.query.filter_by(nip=receipt['shop']['nip']).first()
            if available_shop is not None:
                receipt['shop']['name'] = available_shop.name
                if available_shop.location:
                    receipt['shop']['location'] = db.get_coordinates(available_shop.location)

        return receipt


class Stats(Resource):

    def get(self):
        parser = reqparse.RequestParser()
        parser.add_argument('email', type=str, location='args', required=True)
        args = parser.parse_args(request)
        email = args['email']

        sql = text("select p.name, sum(pe.price) from products p " \
              "join product_aliases_on_recepits pa on pa.product_gtin = p.gtin " \
              "join price_entries pe on pe.product_alias_id = pa.id " \
              "join receipts r on r.id = pe.receipt_id " \
              "where r.user_id = :email " \
              "group by p.name")


        rows = db.get_db().engine.execute(sql, email=email)
        result = []
        for product_name, total_spent in rows:
            result.append({
                'product_name': product_name,
                'total_spent': total_spent
            })

        return result