from flask.ext.sqlalchemy import SQLAlchemy

db = None


def set_up_db(app):
    global db
    db = DatabaseWrapper(SQLAlchemy(app))


class DatabaseWrapper:

    def __init__(self, db):
        self.alch_db = db

    def add(self, row):
        self.alch_db.session.add(row)
        return self.alch_db.session.commit()

    def get_db(self):
        return self.alch_db

    def commit(self):
        self.alch_db.commit()