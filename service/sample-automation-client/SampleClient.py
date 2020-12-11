import json
import time
import random
import os
from pathlib import Path

import requests

from EventGenerator.Generator import Generator


class SampleClient:
    path = Path("EventGenerator/config/")
    username = os.environ.get('SIMULATOR_USER')
    password = os.environ.get('SIMULATOR_PASS')
    generator = Generator(random.randint(1, 5), path)

    def first_contact(self):
        url = "https://jrie.eu:8001/houses/schema"
        payload = json.dumps(json.load(open(self.path / "schema.json")))
        headers = {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
        response = requests.request("PUT", url, headers=headers, data=payload, verify=False,
                                    auth=(self.username, self.password))
        json_response = json.loads(response.text)
        print(json_response)

        devIds_with_types: dict = {}
        typeIds_with_actions: dict = {}
        roomsIds_with_devices: dict = {}

        for deviceType in json_response['deviceTypes']:
            for event in deviceType['events']:
                typeIds_with_actions.setdefault(deviceType['id'], []).append(event['id'])

        for room in json_response['rooms']:
            for device in room['devices']:
                devIds_with_types[device['id']] = device['typeId']

        for room in json_response['rooms']:
            for device in room['devices']:
                roomsIds_with_devices.setdefault(room['id'], []).append(device['id'])

        with open(self.path / 'typeIds_with_actions.json', 'w') as file1:
            file1.write(json.dumps(typeIds_with_actions))

        with open(self.path / 'devIds_with_types.json', 'w') as file2:
            file2.write(json.dumps(devIds_with_types))

        with open(self.path / 'roomsIds_with_devices.json', 'w') as file3:
            file3.write(json.dumps(roomsIds_with_devices))

    def send_data(self):
        url = "https://jrie.eu:8001/events"
        payload = ''
        for event in range(random.randint(10, 30)):
            payload += str(self.generator.generate_event()) + "\r\n"
            time.sleep(random.randint(1, 1))  # change here to generate faster
        headers = {
            'Content-Type': 'text/csv',
        }
        print(payload)
        response = requests.request("POST", url, headers=headers, data=payload, verify=False,
                                    auth=(self.username, self.password))
        print(response.text)
        print(response.status_code)
