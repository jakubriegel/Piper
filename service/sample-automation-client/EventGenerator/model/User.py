from enum import Enum
from random import shuffle, choice, random

from EventGenerator.config.time import HOUR, HALF_HOUR, FIVE_HOURS


class User:

    class State(Enum):
        IN = 1
        OUT = 2
        ASLEEP = 3

    _states_prob = [
        (State.IN, 70),
        (State.OUT, 20),
        (State.ASLEEP, 10),
    ]

    def __init__(self, name: str, room: str, change_state_at: int, next_event_at: int, rooms: []) -> None:
        self.name = name
        self.state = User.State.IN
        self.room = room
        self.change_state_at = change_state_at
        self.next_event_at = next_event_at
        self._rooms = rooms

    def get_user(self):
        return self.name, self.state, self.room, self.change_state_at, self.next_event_at

    def update(self, current_time: int):
        if current_time >= self.change_state_at:
            self._update_state()
        if current_time >= self.next_event_at:
            self._update_next_event_time()

    def _update_state(self):
        states = []
        for s, p in User._states_prob:
            for _ in range(p):
                states.append(s)
        shuffle(states)
        if self.state is not User.State.IN:
            states = list(filter(lambda it: it is not self.state, states))

        self.state = choice(states)

        if self.state is User.State.IN:
            self.change_state_at += int(random() * HOUR)
            self.room = choice(self._rooms)
        elif self.state is User.State.OUT:
            self.change_state_at += int(random() * HOUR)
            self.next_event_at = self.change_state_at
        elif self.state is User.State.ASLEEP:
            self.change_state_at += int(random() * FIVE_HOURS) + FIVE_HOURS
            self.next_event_at = self.change_state_at
        else:
            raise Exception('unknown user state')

    def _update_next_event_time(self):
        self.next_event_at += int(random() * HALF_HOUR)
