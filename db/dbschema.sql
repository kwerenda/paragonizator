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
  location GEOMETRY NOT NULL,
  UNIQUE (name, location)
);

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
  email TEXT PRIMARY KEY,
  last_location GEOMETRY
);

DROP TABLE IF EXISTS receipts CASCADE;
CREATE TABLE receipts (
  id SERIAL PRIMARY KEY,
  user_id text REFERENCES users(email),
  date_added TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS price_entries CASCADE;
CREATE TABLE price_entries (
  product_id BIGINT REFERENCES products(gtin),
  shop_id INTEGER REFERENCES shops(id),
  price REAL NOT NULL,
  receipt_id INTEGER REFERENCES receipts(id)
);
