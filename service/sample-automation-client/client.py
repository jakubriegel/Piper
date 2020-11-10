import json
import time
import requests
import uuid

class Client:
    def __init__(self):


def first_contact():
    url = "https://jrie.eu:8001/houses/schema"
    payload = json.dumps(json.load(open("devices.json")))
    print(payload)
    headers = {
      'Accept': 'application/json',
      'Origin': 'localhost',
      'Authorization': 'Basic aG91c2UtMi1zZXJ2ZXI6c2VjcmV0',
      'Content-Type': 'application/json'
    }

    response = requests.request("PUT", url, headers=headers, data=payload, verify=False)

    print(response.text)

def send_data():
    url = "https://jrie.eu:8001/events"
    while True:
        payload = str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4()) + "\r\n" + \
                  str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4()) + "\r\n" + \
                  str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4()) + "\r\n" + \
                  str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4()) + "\r\n" + \
                  str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4()) + "\r\n" + \
                  str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4()) + "\r\n" + \
                  str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4()) + "\r\n" + \
                  str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4()) + "\r\n" + \
                  str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4()) + "\r\n" + \
                  str(int(time.time())) + "," + str(uuid.uuid4()) + "," + str(uuid.uuid4())
        print(payload)
        headers = {
            'Content-Type': 'text/csv',
            'Authorization': 'Basic aG91c2UtMi1zZXJ2ZXI6c2VjcmV0'
        }
        response = requests.request("POST", url, headers=headers, data=payload, verify=False)
        print(response.text)
        print(response.status_code)


if __name__ == '__main__':
    first_contact()
    # while True:
    #     send_data()
    #     time.sleep(30)
