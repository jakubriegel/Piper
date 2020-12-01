class Event:
    def __init__(self, time, id, action):
        self.time = time
        self.id = id
        self.action = action

    def __getitem__(self, item):
        return self.time, self.id, self.action

    def __str__(self):
        return f'{self.time},{self.id},{self.action}'

    def __hash__(self):
        return hash((self.id, self.action))

    def is_same(self, other: any):
        assert type(other) is Event
        return self.id == other.id and self.action == other.action
