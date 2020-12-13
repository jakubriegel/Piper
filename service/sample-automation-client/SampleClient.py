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
    generator: Generator = None

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

        self.generator = Generator(random.randint(3, 8), self.path)

    def send_data(self):
        if self.generator is not None:
            url = "https://jrie.eu:8001/events"
            payload = ''
            payload += "\r\n".join(self.generator.generate_events(random.randint(10, 30)))
            headers = {
                'Content-Type': 'text/csv',
            }
            response = requests.request("POST", url, headers=headers, data=payload, verify=False,
                                        auth=(self.username, self.password))
            print(response.status_code)
        else:
            self.generator = Generator(random.randint(1, 5), self.path)

    def request_routines(self):
        url = "https://jrie.eu:8001/routines"

        payload = {}
        headers = {
            'Accept': 'application/json',
            'Origin': 'localhost',
            'Authorization': 'Basic b3duZXItMjpzZWNyZXQ='
        }

        response = requests.request("GET", url, headers=headers, data=payload)
        json_response = json.loads(response.text)
