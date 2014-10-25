import json
import os
import os.path
import uuid
from geoalchemy2 import Geometry

import requests
from bs4 import BeautifulSoup
from flask import request
from flask.ext.restful import Resource, reqparse
from server.database import models
from server.database.database import db
from server.database.models import Product, Shop
from server.utils.receipt_ocr import ReceiptOcr
from server.utils.gtin_fetch import GtinFetch


class User(Resource):

    def put(self):
        """

        :return:
        """
        print("Adding new user")
        parser = reqparse.RequestParser()
        parser.add_argument('email', type=str, location='args', required=True)
        args = parser.parse_args(request)
        new_user = models.User(args['email'])
        db.add(new_user)
        return {"message": "User added"}

    def post(self):
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
                usr.last_location = 'POINT({0} {1})'.format(args['loc_lat'], args['loc_long'])
                return {"message": "User location updates"}
            return {"message": "User not found"}
        return {"message": "Location information not found"}


class ShoppingList(Resource):

    def put(self):
        """
        optimizing provided shopping list
        :return: optimal shopping list
        """
        print("Loading shopping list")
        shopping_list_json = request.get_json()
        print(shopping_list_json)

    def get(self):
        return {'test': 'method'}


class Barcode(Resource):

    def put(self):
        """
        Loading barcode to database (barcode as query argument)
        :return:
        """
        print("Reading barcode arguments")
        parser = reqparse.RequestParser()
        parser.add_argument('barcode', type=int, help='Barcode cannot be converted', location='args')
        args = parser.parse_args(request)
        barcode = args["barcode"]
        gtin_fetch = GtinFetch()
        product_name = gtin_fetch.fetch_product_name(barcode)
        new_product = Product(int(barcode), product_name)
        db.add(new_product)
        print("Barcode added")
        return {'message': "Barcode added"}


class Receipt(Resource):

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
        receipt = receipt_ocr.do_ocr()
        # name, address = self.get_company_info(receipt['shop']['nip'])
        # receipt['shop']['name'] = name
        # receipt['shop']['address'] = address
        return receipt

    def put(self):
        """
        Fix receipt mistakes, needs user_id (email) as query parameter
        :return: status of fixed receipt
        """

        parser = reqparse.RequestParser()
        parser.add_argument('user_id', type=str, location='args', required=True)
        args = parser.parse_args(request)

        fixed_receipt = json.load(request.get_json())
        list_of_products = fixed_receipt["products"]
        # nip = fixed_receipt["nip"]
        company_name = fixed_receipt["shop"]["name"]
        location = fixed_receipt["shop"]["location"]  # get POINT from location
        nip = fixed_receipt["shop"]["nip"]

        return {'message': 'saved'}
