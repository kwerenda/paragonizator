import json
from flask.ext.sqlalchemy import SQLAlchemy
from sqlalchemy import func

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

    def get_coordinates(self, geometry):
        geom_json = json.loads(self.alch_db.session.scalar(func.ST_AsGeoJSON(geometry)))
        return geom_json['coordinates']

    def get_x_y(self, geometry):
        coords = self.get_coordinates(geometry)
        return coords[0], coords[1]
