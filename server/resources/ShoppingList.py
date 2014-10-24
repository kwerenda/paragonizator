from flask import request
from flask.ext.restful import Resource


class ShoppingList(Resource):

    def put(self):
        shopping_list_json = request.get_json()

    def get(self):
        return {'test': 'method'}


class Receipt(Resource):

    def put(self):
        loaded_receipt = request.files['file']