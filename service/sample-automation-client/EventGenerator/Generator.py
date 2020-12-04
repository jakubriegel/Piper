import json
import random
from pathlib import Path
from time import time, sleep

from typing import List

from EventGenerator.model.User import User
from EventGenerator.model.Event import Event


class Generator:
    devIds_with_types: dict
    typeIds_with_actions: dict
    roomsIds_with_devices: dict

    events: dict
    current_events: List[Event] = []

    rooms: List[str]
    users: List[User]

    def __init__(self, users_n, path: Path):
        self.devIds_with_types = json.load(open(path / "devIds_with_types.json"))
        self.typeIds_with_actions = json.load(open(path / "typeIds_with_actions.json"))
        self.roomsIds_with_devices = json.load(open(path / "roomsIds_with_devices.json"))
        self.events = self.get_all_possible_events()
        self.rooms = self.get_rooms()
        self.users = self.generate_users(users_n)
        for user in self.users:
            user.update(int(time()))

    def __hash__(self):
        return hash(self.events)

    def block_event(self, event: Event):
        self.current_events.append(event)

    def release_event(self, event: Event):
        self.current_events.remove(event)

    def update_blocked(self):
        for event in self.current_events:
            if event.end() < int(time()):
                self.release_event(event)

    def get_rooms(self) -> List[str]:
        return list(self.roomsIds_with_devices.keys())

    def get_all_possible_events(self):
        all_possible = {}
        for room in self.roomsIds_with_devices:
            devices = self.roomsIds_with_devices[room]
            actions = {}
            for device in devices:
                actions[device] = self.typeIds_with_actions[self.devIds_with_types[device]]
            all_possible[room] = actions
        return all_possible

    def generate(self, room: str, start_time, end_time):
        self.update_blocked()
        events_pool = self.events[room]
        while True:
            device = random.choice(list(events_pool.keys()))
            action = random.choice(list(events_pool[device]))
            event = Event(start_time, end_time, device, action)
            for current_event in self.current_events:
                if event.id == current_event.id and event.action == current_event.action:
                    continue
            return event

    def generate_users(self, n: int):
        current_time: int = int(time())
        return [User(f'user_{i}', random.choice(self.rooms), current_time, current_time, self.rooms) for i in range(n)]

    def generate_event(self):
        user = random.choice(self.users)
        user.update(int(time()))
        event = self.generate(user.room, int(time()), user.next_event_at)
        self.block_event(event)
        return event

    def generate_events(self, events_number: int):
        events: List[Event] = []
        for i in range(events_number):
            events.append(self.generate_event())
            # sleep(random.randint(1, 1))
        events = sorted(events, key=lambda timestamp: timestamp[0])
        string_events = []
        for event in events:
            string_events.append(str(event) + "\r\n")
        return string_events


if __name__ == '__main__':
    generator = Generator(5, Path("config/"))
    print(generator.generate_events(30))


