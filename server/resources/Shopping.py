from flask import request
from flask.ext.restful import Resource


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

    def put(self, product_name, barcode):
        """
        Connecting barcode and product name
        :param product_name:
        :param barcode:
        :return:
        """
        print("Adding barcode")



class Receipt(Resource):

    def post(self):
        """
        Uploads receipt
        :return: status of receipt upload
        """
        uploaded_receipt = request.files['file']
        print(uploaded_receipt.read())
        return {'loaded': True}