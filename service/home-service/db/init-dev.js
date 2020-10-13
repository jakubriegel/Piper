const house = db.getCollection("house")
house.remove({})
house.insert({
    "_id": "house-1",
    "name": "Our Home",
    "pin": "1234"
})

const user = db.getCollection("user")
user.remove({})
user.insert({
    "login":"house-1-server",
    "secret":"{noop}secret",
    "house": "house-1",
    "roles":["HOUSE"]
})
user.insert({
    "login":"owner-1",
    "secret":"{noop}secret",
    "house": "house-1",
    "roles":["USER"]
})

const routine = db.getCollection("routine")
routine.remove({})

const pastEvent = db.getCollection("pastEvent")
pastEvent.remove({})
