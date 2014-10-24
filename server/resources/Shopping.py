from flask import request
from flask.ext.restful import Resource


class ShoppingList(Resource):

    def put(self):
        shopping_list_json = request.get_json()

    def get(self):
        return {'test': 'method'}


class Receipt(Resource):

    def post(self):
        """
        Uploads receipt
        :return: status of receipt upload
        """
        uploaded_receipt = request.files['file']
        print(uploaded_receipt.read())
        return {'loaded': True}