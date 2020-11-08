# Piper home-service

## Features
### Routines management
tba
### Houses schema management
tba
### Models management
#### Model retraining
The service keeps list of current and past models for every house.
With specified interval _home-service_ is triggering creation of new models. 
Retraining process goes as follows:
1. Find all houses.
1. Check if since last training more than _N_ events have been added.
1. If false, finish here.
1. Generate and store id for new model.
1. Send an event for new model creation.
1. Fetch and save event in shared volume.
1. Wait for confirmation on `/models/ready`.
1. Update current model in `House`.
 
## Test Users
login          | password | type  | house   | note
-------------- | -------- | ----- | ------- | ----
owner-1        | secret   | USER  | house-1 | 
owner-2        | secret   | USER  | house-2 | 
owner-3        | secret   | USER  | house-3 | house-3 has no model 
house-1-server | secret   | HOUSE | house-1 |
house-2-server | secret   | HOUSE | house-2 |
house-3-server | secret   | HOUSE | house-3 | house-3 has no model
admin          | secret   | ADMIN | -       | 

## REST API
For documentation check Postman collection under `Piper/docs/postman`
