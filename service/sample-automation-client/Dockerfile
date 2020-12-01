FROM python:3.9

COPY ./requirements.txt /sample-automation-client/requirements.txt
RUN pip install -r /sample-automation-client/requirements.txt

COPY EventGenerator/config/schema.json /sample-automation-client/event-generator/config/schema.json
COPY run.py /sample-automation-client/run.py
COPY ./SampleClient.py /sample-automation-client/SampleClient.py

WORKDIR /sample-automation-client

CMD [ "python", "client.py" ]