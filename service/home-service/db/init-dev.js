function uuid() {
    return `${UUID()}`
}

function now() {
    return new Date()
}

const house = db.getCollection('house')
house.remove({})
const house1Id = uuid()
house.insert({
    '_id': house1Id,
    'name':'Our Home',
    'consents': {
        'behaviourBasedLearning': true
    }
})
const house2Id = uuid()
house.insert({
    '_id': house2Id,
    'name': 'Our Home 2',
    'consents': {
        'behaviourBasedLearning': true
    }
})
const house3Id = uuid()
house.insert({
    '_id': house3Id,
    'name': 'Our Home 3 [no models]',
    'models': {
        'current': null,
        'past': []
    },
    'consents': {
        'behaviourBasedLearning': true
    }
})

const user = db.getCollection('user')
user.remove({})
user.insert({
    'login':'admin',
    'secret':'{noop}secret',
    'house': '',
    'roles':['ADMIN']
})
user.insert({
    'login':'house-1-server',
    'secret':'{noop}secret',
    'house': house1Id,
    'roles':['HOUSE']
})
user.insert({
    'login':'house-2-server',
    'secret':'{noop}secret',
    'house': house2Id,
    'roles':['HOUSE']
})
user.insert({
    'login':'house-3-server',
    'secret':'{noop}secret',
    'house': house3Id,
    'roles':['HOUSE']
})
user.insert({
    'login':'owner-1',
    'secret':'{noop}secret',
    'house': house1Id,
    'roles':['USER']
})
user.insert({
    'login':'owner-2',
    'secret':'{noop}secret',
    'house': house2Id,
    'roles':['USER']
})
user.insert({
    'login':'owner-3',
    'secret':'{noop}secret',
    'house': house3Id,
    'roles':['USER']
})
user.insert({
    'login':'model-builder',
    'secret':'{noop}secret',
    'house': '',
    'roles':['MODEL_BUILDER']
})

const routine = db.getCollection('routine')
routine.remove({})
routine.insert({
    '_id': uuid(),
    'name': 'Morning',
    'houseId': house1Id,
    'enabled': true,
    'events': [],
    'configuration': {
        'days': [],
        'start': null,
        'end': null
    }
})
routine.insert({
    '_id': uuid(),
    'name': 'Evening',
    'houseId': house1Id,
    'enabled': false,
    'events': [],
    'configuration': {
        'days': [],
        'start': null,
        'end': null
    }
})
routine.insert({
    '_id': uuid(),
    'name': 'Week',
    'houseId': house1Id,
    'enabled': true,
    'events': [],
    'configuration': {
        'days': [
            'MONDAY',
            'TUESDAY',
            'WEDNESDAY',
            'THURSDAY',
            'FRIDAY'
        ],
        'start': null,
        'end': null
    }
})
routine.insert({
    '_id': uuid(),
    'name': 'Evening 2',
    'houseId': house2Id,
    'enabled': false,
    'events': [],
    'configuration': {
        'days': [],
        'start': null,
        'end': null
    }
})
routine.insert({
    '_id': uuid(),
    'name': 'Evening 3',
    'houseId': house3Id,
    'enabled': false,
    'events': [],
    'configuration': {
        'days': [],
        'start': null,
        'end': null
    }
})

const pastEvent = db.getCollection('pastEvent')
pastEvent.remove({})

for (let i = 0; i < 1000; i++) {
    pastEvent.insert({
        'houseId': house1Id,
        'deviceId': uuid(),
        'eventId': uuid(),
        'time': now()
    })

    pastEvent.insert({
        'houseId': house2Id,
        'deviceId': uuid(),
        'eventId': uuid(),
        'time': now()
    })
}

const deviceEvent = db.getCollection('deviceEvent')
deviceEvent.remove({})

const deviceType = db.getCollection('deviceType')
deviceType.remove({})

const room = db.getCollection('room')
room.remove({})

const device = db.getCollection('device')
device.remove({})

const model = db.getCollection('model')
model.remove({})

model.insert({
    '_id': uuid(),
    'stagedAt': now(),
    'createdAt': now(),
    'houseId': house1Id
})

model.insert({
    '_id': uuid(),
    'stagedAt': now(),
    'createdAt': now(),
    'houseId': house2Id
})
