# Piper home-service

## Test Users
login          | password | type  | house   | note
-------------- | -------- | ----- | ------- | ----
owner-1        | secret   | USER  | house-1 | 
owner-2        | secret   | USER  | house-2 | 
owner-3        | secret   | USER  | house-3 | house-3 has no model 
house-1-server | secret   | HOUSE | house-1 |
house-2-server | secret   | HOUSE | house-2 |
house-3-server | secret   | HOUSE | house-3 | house-3 has no model

## REST API
### Routines
#### Get house's routines 
##### Path
`GET /routines`

##### Parameters
* enabled - `[true, false]`

##### Request
```
GET /routines
Accept: application/json
Authorization: Basic abc:123
```

##### Response
```
200 OK
Content-Type: application/json
{
    "routines": [
        {
            "id": "782797fd-bdac-4801-8a0f-f7f0f215eca0",
            "name": "Morning",
            "enabled": true
        }
    ],
    "links": [
        {
            "rel": "self",
            "href": "/routines"
        },
        {
            "rel": "first",
            "href": "/routines/782797fd-bdac-4801-8a0f-f7f0f215eca0"
        },
        {
            "rel": "describes",
            "href": "/houses"
        }
    ]
}
```

##### Statuses
none

#### Get routine by id
##### Path
`GET /routines/<routineId>`

##### Parameters
none

##### Request
```
GET /routines/<routineId>
Accept: application/json
Authorization: Basic abc:123
```

##### Response
```
200 OK
Content-Type: application/json
{
    "routine": {
        "id": "782797fd-bdac-4801-8a0f-f7f0f215eca0",
        "name": "Morning",
        "enabled": true,
        "events": [],
        "configuration": {
            "days": [],
            "start": null,
            "end": null
        }
    },
    "links": [
        {
            "rel": "self",
            "href": "/routines/782797fd-bdac-4801-8a0f-f7f0f215eca0"
        },
        {
            "rel": "edit",
            "href": "/routines/782797fd-bdac-4801-8a0f-f7f0f215eca0"
        },
        {
            "rel": "collection",
            "href": "/routines"
        },
        {
            "rel": "describes",
            "href": "/houses"
        }
    ]
}
```

##### Statuses
none

#### Add routine
##### Path
`POST /routines`

##### Parameters
none

##### Request
```
GET /routines
Accept: application/json
Content-Type: application/json
Authorization: Basic abc:123
{
  "name": "custom {{$timestamp}}",
  "enabled": null,
  "events": [
    {
      "trigger": "kitchen_lamp_1",
      "action": "light_on"
    }
  ],
  "configuration": {
    "days": [],
    "start": null,
    "end": null
  }
}
```

##### Response
```
200 OK
Content-Type: application/json
{
    "routine": {
        "id": "473dc47b-3365-41dd-a2bb-540350d375bd",
        "name": "custom 1603228120",
        "enabled": false,
        "events": [
            {
                "trigger": "kitchen_lamp_1",
                "action": "light_on"
            }
        ],
        "configuration": {
            "days": [],
            "start": null,
            "end": null
        }
    },
    "links": [
        {
            "rel": "self",
            "href": "/routines/473dc47b-3365-41dd-a2bb-540350d375bd"
        },
        {
            "rel": "edit",
            "href": "/routines/473dc47b-3365-41dd-a2bb-540350d375bd"
        },
        {
            "rel": "collection",
            "href": "/routines"
        },
        {
            "rel": "describes",
            "href": "/houses"
        }
    ]
}
```

##### Statuses
none

#### Modify routine
##### Path
`PUT /routines/<routineId>`

##### Parameters
none

##### Request
```
GET /routines
Accept: application/json
Content-Type: application/json
Authorization: Basic abc:123
{
  "name": "custom {{$timestamp}}",
  "enabled": null,
  "events": [
    {
      "trigger": "kitchen_lamp_1",
      "action": "light_on"
    }
  ],
  "configuration": {
    "days": [],
    "start": null,
    "end": null
  }
}
```

##### Response
```
200 OK
Content-Type: application/json
{
    "routine": {
        "id": "473dc47b-3365-41dd-a2bb-540350d375bd",
        "name": "custom 1603228120",
        "enabled": false,
        "events": [
            {
                "trigger": "kitchen_lamp_1",
                "action": "light_on"
            }
        ],
        "configuration": {
            "days": [],
            "start": null,
            "end": null
        }
    },
    "links": [
        {
            "rel": "self",
            "href": "/routines/473dc47b-3365-41dd-a2bb-540350d375bd"
        },
        {
            "rel": "edit",
            "href": "/routines/473dc47b-3365-41dd-a2bb-540350d375bd"
        },
        {
            "rel": "collection",
            "href": "/routines"
        },
        {
            "rel": "describes",
            "href": "/houses"
        }
    ]
}
```

#### Get suggestions for next events
##### Path
`POST /routines/suggestions`

##### Parameters
* `trigger` - required
* `action` - required
* `limit` - required

##### Request
```
GET /routines/suggestions?trigger=sample_trigger_1&action=light_off&limit=3
Accept: application/json
Authorization: Basic abc:123
```

##### Response
```
200 OK
Content-Type: application/json
{
    "start": {
        "trigger": "sample_trigger_1",
        "action": "light_off"
    },
    "suggestions": [
        {
            "trigger": "4691_sample_trigger_1",
            "action": "light_off"
        },
        {
            "trigger": "975_sample_trigger_1",
            "action": "light_off"
        },
        {
            "trigger": "1151_sample_trigger_1",
            "action": "light_off"
        }
    ],
    "n": 3,
    "links": [
        {
            "rel": "self",
            "href": "/routines/suggestions?trigger=sample_trigger_1&trigger=light_off&limit=3"
        },
        {
            "rel": "collection",
            "href": "/routines"
        }
    ]
}
```
##### Statuses
* 204 - no suggestions returned due to no model available for user's house. Note that is model returns no suggestions an empty list would be returned.

### Events
#### Upload recent events
##### Path
`POST /events`

##### Parameters
none

##### Request
```
GET /routines
Content-Type: text/csv
Authorization: Basic abc:123
1589054854,kitchen_light_1_switch,light_on
1589055229,kitchen_light_2_switch,light_on
1589055809,kitchen_light_3_switch,light_on
1589056736,kitchen_blind_1_switch,blind_up
1589057023,kitchen_light_2_switch,light_off
1589057233,kitchen_light_2_switch,light_on
```

##### Response
```
200 OK
```

##### Statuses
* 422 - on invalid row, server will parse and save all valid events and return invalid rows in JSON response

### Houses

#### Get user's house 
##### Path
`GET /houses`

##### Parameters
none

##### Request
```
GET /houses
Accept: application/json
Authorization: Basic abc:123
```

##### Response
```
200 OK
Content-Type: application/json
{
    "name": "Our Home",
    "consents": {
        "behaviourBasedLearning": true
    },
    "links": [
        {
            "rel": "self",
            "href": "/houses"
        },
        {
            "rel": "describedBy",
            "href": "/routines"
        }
    ]
}
```

##### Statuses
none
