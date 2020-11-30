# Piper intelligence-core-service
Piper will be an AI based system, that will be able to propose automated routines suggestions based on real life events.

## Deployment

### Local
Create `venv` environment and install dependencies by running these commands: 
```
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
python3 -u api.py
```
### Docker
Just run docker container :) 
```
docker-compose -d intelligence-core-service
```
