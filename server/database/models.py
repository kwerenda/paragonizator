from geoalchemy2 import Geometry
from sqlalchemy import func
from server.database.database import db as wrapped_db

db = wrapped_db.get_db()


class User(db.Model):
    __tablename__ = "users"
    email = db.Column(db.String(120), primary_key=True)
    last_location = db.Column(Geometry('POINT', srid=4326))
    receipts = db.relationship('Receipt', backref='users', lazy='dynamic')

    def __init__(self, email, last_location=None):
        self.email = email
        self.last_location = last_location

    def __repr__(self):
        return '<User {0}>'.format(self.email)


class Receipt(db.Model):
    __tablename__ = "receipts"
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.String(120), db.ForeignKey('users.email'))
    date_added = db.Column(db.DateTime, default=func.now())
    shop_id = db.Column(db.Integer, db.ForeignKey('shops.id'))
    price_entries = db.relationship('PriceEntry', backref='receipts', lazy='dynamic')

    def __init__(self, user_id, shop_id, date_added=None):
        self.shop_id = shop_id
        self.user_id = user_id
        self.date_added = date_added

    def __repr__(self):
        return '<Receipt {0} from user {1}>'.format(self.date_added, self.user_id)


class Shop(db.Model):
    __tablename__ = "shops"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=False)
    location = db.Column(Geometry('POINT', srid=4326), nullable=False)
    nip = db.Column(db.String(50))
    price_entries = db.relationship('Receipt', backref='shops', lazy='dynamic')

    def __init__(self, name, location, nip=None):
        self.location = location
        self.name = name
        self.nip = nip

    def __repr__(self):
        return '<Shop {0} at {1}>'.format(self.name, self.location)


class PriceEntry(db.Model):
    __tablename__ = "price_entries"
    product_id = db.Column(db.Integer, db.ForeignKey('products.gtin'), primary_key=True)
    receipt_id = db.Column(db.Integer, db.ForeignKey('receipts.id'), primary_key=True)
    price = db.Column(db.Float)
    quantity = db.Column(db.Float)
    unit = db.Column(db.String("50"), db.ForeignKey('units.name'))

    def __init__(self, product_id, shop_id, receipt_id, price, quantity, unit):
        if quantity:
            self.quantity = quantity
        else:
            self.quantity = 1.0
        if unit:
            self.unit = unit
        else:
            self.unit = "pieces"
        self.price = price
        self.shop_id = shop_id
        self.product_id = product_id
        self.receipt_id = receipt_id

    def __repr__(self):
        return '<Price {0}>'.format(self.price)


class Product(db.Model):
    __tablename__ = "products"
    id = db.Column(db.Integer, primary_key=True)
    gtin = db.Column(db.Integer)
    name = db.Column(db.String(120), nullable=False)
    price_entries = db.relationship('PriceEntry', backref='products', lazy='dynamic')

    def __init__(self, name, gtin=None):
        self.gtin = gtin
        self.name = name

    def __repr__(self):
        return '<Price {0}>'.format(self.price)


class Unit(db.Model):
    __tablename__ = "units"
    name = db.Column(db.String(50), primary_key=True)
    price_entries = db.relationship('PriceEntry', backref="units", lazy='dynamic')

    def __init__(self, name):
        self.name = name

    def __repr__(self):
        return '<Unit {0}>'.format(self.name)