import json
import time
import random
from pathlib import Path

import requests
import sys


class SampleClient:
    devIds_with_types: dict = {}
    typeIds_with_actions: dict = {}

    def load_devices(self):

        self.devIds_with_types = json.load(open("devIds_with_types.json"))
        self.typeIds_with_actions = json.load(open("typeIds_with_actions.json"))

    def first_contact(self):
        url = "https://jrie.eu:8001/houses/schema"
        payload = json.dumps(json.load(open("devices.json")))
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

        with open('typeIds_with_actions.json', 'w') as file1:
            file1.write(json.dumps(self.typeIds_with_actions))

        with open('devIds_with_types.json', 'w') as file2:
            file2.write(json.dumps(self.devIds_with_types))

    def send_data(self):
        url = "https://jrie.eu:8001/events"
        payload = ''
        for event in range(random.randint(10, 30)):
            device, devType = random.choice(list(self.devIds_with_types.items()))
            event = random.choice(self.typeIds_with_actions[devType])
            payload += str(int(time.time())) + "," + device + "," + event + "\r\n"
            time.sleep(random.randint(1, 300))  # change here to generate faster
        headers = {
            'Content-Type': 'text/csv',
            'Authorization': 'Basic aG91c2UtMi1zZXJ2ZXI6c2VjcmV0'
        }
        response = requests.request("POST", url, headers=headers, data=payload, verify=False)
        print(response.text)
        print(response.status_code)


if __name__ == '__main__':
    client = SampleClient()
    if len(sys.argv) > 1 and sys.argv[1] == '--reset':
        client.first_contact()
    else:
        if Path('devIds_with_types.json').is_file() and Path('typeIds_with_actions.json').is_file():
            client.load_devices()
        else:
            client.first_contact()
    while True:
        client.send_data()
