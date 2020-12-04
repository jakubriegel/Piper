class Event:
    def __init__(self, time, end_time, id, action):
        self.time = time
        self.end_time = end_time
        self.id = id
        self.action = action

    def end(self):
        return self.end_time

    def __getitem__(self, item):
        return self.time, self.id, self.action

    def __str__(self):
        return f'{self.time},{self.id},{self.action}'

    def __hash__(self):
        return hash((self.id, self.action))

    def __eq__(self, other):
        assert type(other) is Event
        return self.id == other.id and self.action == other.action
