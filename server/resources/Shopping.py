import json

import requests
from bs4 import BeautifulSoup
from flask import request
from flask.ext.restful import Resource, reqparse
from server.database.database import db
from server.database.models import Product


class User(Resource):

    def put(self):
        pass


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
        Connecting barcode and product name
        :return:
        """
        print("Reading barcode arguments")
        parser = reqparse.RequestParser()
        parser.add_argument('barcode', type=int, help='Barcode cannot be converted', location='args')
        parser.add_argument('product_name', type=str, location='args')
        args = parser.parse_args(request)
        barcode = args["barcode"]
        product_name = args["product_name"]
        new_product = Product(int(barcode), product_name)
        db.add(new_product)
        print("Barcode added")
        return {'message': "Barcode added"}


class Receipt(Resource):

    def get_company_info(self, nip):
        url = 'http://www.money.pl/rejestr-firm/nip/{0}'.format(nip)
        response = requests.get(url)
        print(response)

    def post(self):
        """
        Uploads receipt
        :return: ocr'ed receipt to fix mistakes
        """
        uploaded_receipt = request.files['file']
        print(uploaded_receipt.read())
        nip = "101-00-04-069"
        name, address = self.get_company_info(nip)
        return {'message': 'returning JSON for fixing mistakes'}

    def put(self):
        """
        Fix receipt mistakes
        :return: status of fixed receipt
        """
        fixed_receipt = json.load(request.get_json())
        list_of_products = fixed_receipt["products"]
        nip = fixed_receipt["nip"]
        company_name = fixed_receipt["company_name"]
        location = fixed_receipt["location"]

        return {'message': 'saved'}
