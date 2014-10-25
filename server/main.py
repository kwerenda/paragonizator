import json
from flask import Flask, make_response
from flask.ext import restful
from server.database.database import set_up_db

app = Flask(__name__)
app.config.from_object('config')
app.config["SQLALCHEMY_DATABASE_URI"] = "postgresql://mobileapp:agh@172.27.0.14:5432/paragonizator"
set_up_db(app)
api = restful.Api(app)

from server.resources.Shopping import Receipt, ShoppingList, Barcode

api.add_resource(ShoppingList, "/api/shopping_list")
api.add_resource(Receipt, "/api/receipt")
api.add_resource(Barcode, "/api/barcode")

@api.representation('application/json')
def make_json(data, code, headers=None):
    resp = make_response(json.dumps(data), code)
    resp.headers.extend(headers or {})
    return resp

app.run()