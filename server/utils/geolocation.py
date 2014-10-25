import requests


def reverse_geocoding(lat, long, to_get):
    return "NOT FINISHED"
    url = "http://nominatim.openstreetmap.org/reverse?format=json&lat={0}&lon={1}&zoom=18&addressdetails=1".format(lat, long)
    r = requests.get(url)
    if r.status_code == 200:
        address_json = r.json()
        for part in to_get:
            address_json.get("address")

# def geocoding(address):