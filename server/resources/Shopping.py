from flask import request
from flask.ext.restful import Resource, reqparse
from server.database.database import db
from server.database.models import Product


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
        print("PREARGS")
        print(request.values)
        try:
            args = parser.parse_args(request)
            print("ARGS")
            print(args)
            barcode = args["barcode"]
            product_name = args["product_name"]
            print("Adding barcode")
            new_product = Product(int(barcode), product_name)
            db.add(new_product)
            print("Barcode added")
            return {'message': "Barcode added"}
        except Exception as e:
            print(e)



class Receipt(Resource):

    def post(self):
        """
        Uploads receipt
        :return: status of receipt upload
        """
        uploaded_receipt = request.files['file']
        print(uploaded_receipt.read())
        return {'loaded': True}