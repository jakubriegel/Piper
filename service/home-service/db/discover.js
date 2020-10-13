const house = db.getCollection("house")
house.remove({})
house.find({})

const user = db.getCollection("user")
user.remove({})
user.find({})

const routine = db.getCollection("routine")
routine.remove({})
routine.find({})

const pastEvent = db.getCollection("pastEvent")
pastEvent.remove({})
pastEvent.find({})
