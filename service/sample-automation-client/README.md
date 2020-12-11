# Piper sample-automation-client
Piper will be an AI based system, that will be able to propose automated routines suggestions based on real life events.

## How to use run in console:
first use: `python client.py --reset` if you want to receive devices IDs from Piper.  
continue: `python client.py` if you already have your devices IDs.  
remember to keep your `schema.json` in the same directory as `client.py`

## How to use with Docker
### Build
NOTICE: keep `Dockerfile`, `client.py`, `schema.json` and `requirements.txt` in the same directory.  
build: `docker build -t sample-automation-client .`
### Run
run:`docker run --env SIMULATOR_USER=[user] --env SIMULATOR_PASS=[password] sample-automation-client`  
where `SIMULATOR_USER` and `SIMULATOR_PASSWORD` stand for Piper domain login data.