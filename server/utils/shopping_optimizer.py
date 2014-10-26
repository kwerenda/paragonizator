__author__ = 'michal'

from server.database.database import db
from server.database import models
from sqlalchemy.sql import text

class ShoppingOptimizer:

    def __init__(self):
        self.best_price = None
        self.best_shop_set = None


    def findOptimalShopSet(self, email, radius, shops_limit, products):

        arr = ",".join(map(lambda p: "'%" + p + "%'", products))

        sql = text("select p.gtin, p.name, s.id, s.name, pe.price, ST_X(s.location) as latitude, "
                   "ST_Y(s.location) as longitude from products p "
                   "join product_aliases_on_recepits pa on p.gtin = pa.product_gtin "
                   "join price_entries pe on pe.product_alias_id = pa.id "
                   "join shops s on s.id = pa.shop_id "
                   "where ST_DWithin(Geography(s.location), "
                   "(select last_location from users where email = :email ), :radius)"
                   "and p.name ilike any (array[%s])" % (arr, ))

        print(arr)

        rows = db.get_db().engine.execute(sql, email=email, radius=radius, arr=arr)
        product_names = {}
        shop_info = {}
        products = {}

        for row in rows:
            gtin, product_name, shop_id, shop_name, price, latitude, longitude = row

            if gtin not in product_names:
                product_names[gtin] = product_name

            if shop_id not in shop_info:
                shop_info[shop_id] = (shop_name, latitude, longitude)

            if gtin not in products:
                products[gtin] = []
            products[gtin].append((shop_id, price))

        print(products)

        self.bestSet(list(products.keys()), products, [], shops_limit, [])

        shops_result = {}
        if self.best_shop_set is not None:
            for product, shop_id, price in self.best_shop_set:
                shop_name, latitude, longitude = shop_info[shop_id]
                if shop_id not in shops_result:
                    shops_result[shop_id] = {
                        'shop': {
                            'name': shop_name,
                            'longitude': longitude,
                            'latitude': latitude},
                        'products': []
                    }

                shops_result[shop_id]['products'].append({
                    'name': product_names[product],
                    'price': price,
                    'gtin': product
                })

        return list(shops_result.values())

    def bestSet(self, product_list, products, used_shops, shops_limit, shop_set):

        if len(product_list) == 0:
            price_sum = 0
            for product, shop_id, price in shop_set:
                price_sum += price

            if self.best_price is None or price_sum < self.best_price:
                self.best_price = price_sum
                self.best_shop_set = shop_set

            return

        p = product_list[0]
        for s in products[p]:
            if s[0] in used_shops or len(used_shops) < shops_limit:
                new_used_shops = used_shops[:]
                if s[0] not in new_used_shops:
                    new_used_shops.append(s[0])

                new_shop_set = shop_set[:]
                new_shop_set.append((p, s[0], s[1]))

                self.bestSet(product_list[1:], products, new_used_shops, shops_limit, new_shop_set)

#products =