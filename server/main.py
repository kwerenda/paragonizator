import json
from flask import Flask, make_response
from flask.ext import restful
from flask.ext.sqlalchemy import SQLAlchemy
from server.resources.Shopping import Receipt, ShoppingList

app = Flask(__name__)
app.config.from_object('config')
app.config["SQLALCHEMY_DATABASE_URI"] = "postgresql://mobileapp:agh@172.27.0.14:5432/paragonizator"

db = SQLAlchemy(app)
api = restful.Api(app)

api.add_resource(ShoppingList, "/api/v1/shopping_list")
api.add_resource(Receipt, "/api/v1/receipt")

@api.representation('application/json')
def make_json(data, code, headers=None):
    resp = make_response(json.dumps(data), code)
    resp.headers.extend(headers or {})
    return resp

app.run()