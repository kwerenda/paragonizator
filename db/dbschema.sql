CREATE EXTENSION IF NOT EXISTS postgis; -- PostGis extension must be installed in Postgres cluster

DROP TABLE IF EXISTS products CASCADE;
CREATE TABLE products (
  gtin BIGINT PRIMARY KEY, -- global trade item number
  name TEXT NOT NULL
);

DROP TABLE IF EXISTS shops CASCADE;
CREATE TABLE shops (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  location GEOMETRY(POINT,4326) NOT NULL,
  nip text, -- not an ID because sometimes chains have same nip
  UNIQUE (name, location)
);

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
  email TEXT PRIMARY KEY,
  last_location GEOMETRY(Point,4326)
);

DROP TABLE IF EXISTS receipts CASCADE;
CREATE TABLE receipts (
  id SERIAL PRIMARY KEY,
  user_id text REFERENCES users(email),
  date_added TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS units CASCADE;
CREATE TABLE units (
  name TEXT PRIMARY KEY
);

INSERT INTO units (name) VALUES
  ('pieces'),
  ('kg'),
  ('l');


DROP TABLE IF EXISTS price_entries CASCADE;
CREATE TABLE price_entries (
  price REAL NOT NULL,
  product_id BIGINT REFERENCES products(gtin),
  shop_id INTEGER REFERENCES shops(id),
  receipt_id INTEGER REFERENCES receipts(id),
  quantity REAL DEFAULT 1.00, -- pices or eg. weight: 0.7 kg of banans
  unit text REFERENCES units(name) DEFAULT 'pieces'

);
