# Piper sample-automation-client
Sample client for Piper sends a schema of house to Piper and receives the schema with IDs provided by Piper service.  
Next it generates events based on received IDs. Generator takes users, timestamps, and current events into account in order to provide the most real-like data.  
At the end client asks Piper for the Machine Learning proposed routines.

## How to use run in console:
NOTICE: remember to keep your `schema.json` in the `config` directory.
first use: `python run.py --reset` if you want to receive devices IDs from Piper.  
continue: `python run.py` if you already have your devices IDs.  

## How to use with Docker
### Build
NOTICE: keep `Dockerfile`, `run.py` and `requirements.txt` in the same directory. `schema.json` should be in `config` directory.  
build: `docker build -t sample-automation-client .`
### Run
run:`docker run --env SIMULATOR_USER=[user] --env SIMULATOR_PASS=[password] --env SIMULATOR_MODE=[mode] sample-automation-client`  
where `SIMULATOR_USER` and `SIMULATOR_PASSWORD` stand for Piper domain login data.  
`SIMULATOR_MODE` is optional - set `fast` for fast event generation, otherwise ignore.