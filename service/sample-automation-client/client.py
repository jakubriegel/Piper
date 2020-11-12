import json
import time
import random

import requests
import uuid


class SampleClient:
    devIds_with_types: dict = {}
    typeIds_with_actions: dict = {}

    def first_contact(self):
        url = "https://jrie.eu:8001/houses/schema"
        payload = json.dumps(json.load(open("devices.json")))
        print(payload)
        headers = {
            'Accept': 'application/json',
            'Authorization': 'Basic aG91c2UtMi1zZXJ2ZXI6c2VjcmV0',
            'Content-Type': 'application/json'
        }

        response = requests.request("PUT", url, headers=headers, data=payload, verify=False)
        json_response = json.loads(response.text)

        for deviceType in json_response['deviceTypes']:
            for event in deviceType['events']:
                self.typeIds_with_actions.setdefault(deviceType['id'], []).append(event['id'])

        for room in json_response['rooms']:
            for device in room['devices']:
                self.devIds_with_types[device['id']] = device['typeId']

    def send_data(self):
        url = "https://jrie.eu:8001/events"
        payload = ''
        for event in range(random.randint(10, 30)):
            device, devType = random.choice(list(self.devIds_with_types.items()))
            event = random.choice(self.typeIds_with_actions[devType])
            payload += str(int(time.time())) + "," + device + "," + event + "\r\n"
        print(payload)
        headers = {
            'Content-Type': 'text/csv',
            'Authorization': 'Basic aG91c2UtMi1zZXJ2ZXI6c2VjcmV0'
        }
        response = requests.request("POST", url, headers=headers, data=payload, verify=False)
        print(response.text)
        print(response.status_code)


if __name__ == '__main__':
    client = SampleClient()
    client.first_contact()
    while True:
        client.send_data()
        time.sleep(random.randint(10, 30))
