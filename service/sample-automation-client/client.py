import time

import requests
import uuid


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
        'Authorization': 'Basic aG91c2UtMS1zZXJ2ZXI6c2VjcmV0'
    }

    response = requests.request("POST", url, headers=headers, data=payload, verify=False)

    print(response.text)
    print(response.status_code)
    time.sleep(30)
