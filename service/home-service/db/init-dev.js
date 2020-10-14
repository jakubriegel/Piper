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
routine.insert({
    "name": "Morning",
    "houseId": "house-1",
    "modelId": "924aa692-b16a-4887-ad01-52026d711cd2",
    "enabled": true,
    "events": [],
    "configuration": {
        "days": [],
        "start": null,
        "end": null
    }
})
routine.insert({
    "name": "Evening",
    "houseId": "house-1",
    "modelId": "924aa692-b16a-4887-ad01-52026d711cd2",
    "enabled": false,
    "events": [],
    "configuration": {
        "days": [],
        "start": null,
        "end": null
    }
})
routine.insert({
    "name": "Week",
    "houseId": "house-1",
    "modelId": null,
    "enabled": true,
    "events": [],
    "configuration": {
        "days": [
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY"
        ],
        "start": null,
        "end": null
    }
})

const pastEvent = db.getCollection("pastEvent")
pastEvent.remove({})
