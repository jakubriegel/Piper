const house = db.getCollection("house")
house.remove({})
house.insert({
    "_id":"house-1",
    "name":"Our Home",
    "models": {
        "current": {
            "id": "up-to-date-model-id",
            "createdAt": "2020-10-18T16:02:33.882Z"
        },
        "past": [
            {
                "id": "past-model-id",
                "createdAt": "2020-09-18T16:02:33.882Z"
            },
            {
                "id": "past-model-model-id",
                "createdAt": "2020-08-18T16:02:33.882Z"
            }
        ]
    },
    "consents": {
        "behaviourBasedLearning": true
    }
})
house.insert({
    "_id": "house-2",
    "name": "Our Home 2",
    "models": {
        "current": {
            "id": "up-to-date-model-id-II",
            "createdAt": "2020-10-18T16:02:33.882Z"
        },
        "past": [
            {
                "id": "past-model-id-II",
                "createdAt": "2020-09-18T16:02:33.882Z"
            },
            {
                "id": "past-model-model-id-II",
                "createdAt": "2020-08-18T16:02:33.882Z"
            },
        ]
    },
    "consents": {
        "behaviourBasedLearning": true
    }
})
house.insert({
    "_id": "house-3",
    "name": "Our Home 3 [no models]",
    "models": {
        "current": null,
        "past": []
    },
    "consents": {
        "behaviourBasedLearning": true
    }
})

const user = db.getCollection("user")
user.remove({})
user.insert({
    "login":"admin",
    "secret":"{noop}secret",
    "house": "",
    "roles":["ADMIN"]
})
user.insert({
    "login":"house-1-server",
    "secret":"{noop}secret",
    "house": "house-1",
    "roles":["HOUSE"]
})
user.insert({
    "login":"house-2-server",
    "secret":"{noop}secret",
    "house": "house-2",
    "roles":["HOUSE"]
})
user.insert({
    "login":"house-3-server",
    "secret":"{noop}secret",
    "house": "house-3",
    "roles":["HOUSE"]
})
user.insert({
    "login":"owner-1",
    "secret":"{noop}secret",
    "house": "house-1",
    "roles":["USER"]
})
user.insert({
    "login":"owner-2",
    "secret":"{noop}secret",
    "house": "house-2",
    "roles":["USER"]
})
user.insert({
    "login":"owner-3",
    "secret":"{noop}secret",
    "house": "house-3",
    "roles":["USER"]
})

const routine = db.getCollection("routine")
routine.remove({})
routine.insert({
    "_id": "782797fd-bdac-4801-8a0f-f7f0f215eca0",
    "name": "Morning",
    "houseId": "house-1",
    "enabled": true,
    "events": [],
    "configuration": {
        "days": [],
        "start": null,
        "end": null
    }
})
routine.insert({
    "_id": "3624fbe8-054a-4ff4-a826-4fd6b25f19e2",
    "name": "Evening",
    "houseId": "house-1",
    "enabled": false,
    "events": [],
    "configuration": {
        "days": [],
        "start": null,
        "end": null
    }
})
routine.insert({
    "_id": "922278eb-0d8b-40cc-ab85-afe85fc67339",
    "name": "Week",
    "houseId": "house-1",
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
routine.insert({
    "_id": "ba7fa1ba-100a-4b16-94ce-f827ef7e1f74",
    "name": "Evening 2",
    "houseId": "house-2",
    "enabled": false,
    "events": [],
    "configuration": {
        "days": [],
        "start": null,
        "end": null
    }
})
routine.insert({
    "_id": "12dcd3ee-2199-4c3e-bf8e-df2fd2deb672",
    "name": "Evening 3",
    "houseId": "house-3",
    "enabled": false,
    "events": [],
    "configuration": {
        "days": [],
        "start": null,
        "end": null
    }
})

const pastEvent = db.getCollection("pastEvent")
pastEvent.remove({})

const deviceEvent = db.getCollection("deviceEvent")
deviceEvent.remove({})

const deviceType = db.getCollection("deviceType")
deviceType.remove({})

const room = db.getCollection("room")
room.remove({})

const device = db.getCollection("device")
device.remove({})
