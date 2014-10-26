import json
from flask import Flask, make_response, render_template
from flask.ext import restful
from server.database.database import set_up_db

import os

tmpl_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'templates')

app = Flask(__name__, template_folder=tmpl_dir)
app.config.from_object('config')
app.config["SQLALCHEMY_DATABASE_URI"] = "postgresql://mobileapp:agh@172.27.0.14:5432/paragonizator"
set_up_db(app)
api = restful.Api(app)

from server.resources.Shopping import Receipt, ShoppingList, Gtin, Ocr, User, Stats
from server.utils.data_gen import DataGen

api.add_resource(User, "/api/user")
api.add_resource(ShoppingList, "/api/shopping_list")
api.add_resource(Ocr, "/api/ocr")
api.add_resource(Receipt, "/api/add_receipt")
api.add_resource(Gtin, "/api/gtin")
api.add_resource(DataGen, "/api/dont_touch/gen_data")
api.add_resource(Stats, "/api/stats")

@app.route("/")
def index():
    return render_template('index.html')

@api.representation('application/json')
def make_json(data, code, headers=None):
    resp = make_response(json.dumps(data), code)
    resp.headers.extend(headers or {})
    return resp



app.run(host= '0.0.0.0')