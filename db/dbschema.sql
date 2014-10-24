DROP TABLE IF EXISTS products CASCADE;
CREATE TABLE products (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL
);

DROP TABLE IF EXISTS shops CASCADE;
CREATE TABLE shops (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  localisation TEXT NOT NULL, -- change to loc
  UNIQUE (name, localisation)
);

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
  email TEXT PRIMARY KEY,
  last_localisation TEXT -- change to loc
);

DROP TABLE IF EXISTS receipts CASCADE;
CREATE TABLE receipts (
  id SERIAL PRIMARY KEY,
  user_id text REFERENCES users(email),
  date_added TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS price_entries CASCADE;
CREATE TABLE price_entries (
  product_id INTEGER REFERENCES products(id),
  shop_id INTEGER REFERENCES shops(id),
  price REAL NOT NULL,
  receipt_id INTEGER REFERENCES receipts(id)
);
