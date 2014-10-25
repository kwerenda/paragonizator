from server.main import db


class User(db.Model):
    __tablename__ = "users"
    email = db.Column(db.String(120), primary_key=True)
    last_location = db.Column(db.String)
    receipts = db.relationship('Receipt', backref='users', lazy='dynamic')

    def __init__(self, email, last_location):
        self.email = email
        self.last_location = last_location

    def __repr__(self):
        return '<User {0}>'.format(self.email)


class Receipt(db.Model):
    __tablename__ = "receipts"
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.String(120), db.ForeignKey('users.email'))
    date_added = db.Column(db.DateTime)
    price_entries = db.relationship('PriceEntry', backref='receipts', lazy='dynamic')

    def __init__(self, user_id, date_added):
        self.user_id = user_id
        self.date_added = date_added

    def __repr__(self):
        return '<Receipt {0} from user {1}>'.format(self.date_added, self.user_id)


class Shop(db.Model):
    __tablename__ = "shops"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=False)
    localisation = db.Column(db.String(120), nullable=False)
    price_entries = db.relationship('PriceEntry', backref='shops', lazy='dynamic')

    def __init__(self, name, localisation):
        self.localisation = localisation
        self.name = name

    def __repr__(self):
        return '<Shop {0} at {1}>'.format(self.name, self.localisation)


class PriceEntry(db.Model):
    __tablename__ = "price_entries"
    product_id = db.Column(db.Integer, primary_key=True)
    shop_id = db.Column(db.Integer, primary_key=True)
    receipt_id = db.Column(db.Integer, primary_key=True)
    price = db.Column(db.Float)

    def __init__(self, product_id, shop_id, receipt_id, price):
        self.price = price
        self.shop_id = shop_id
        self.product_id = product_id
        self.receipt_id = receipt_id

    def __repr__(self):
        return '<Price {0}>'.format(self.price)


class Product(db.Model):
    __tablename__ = "products"
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=False)
    price_entries = db.relationship('Product', backref='products', lazy='dynamic')