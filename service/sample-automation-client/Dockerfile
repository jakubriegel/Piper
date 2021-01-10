FROM python:3.9

COPY ./requirements.txt /sample-automation-client/requirements.txt
RUN pip install -r /sample-automation-client/requirements.txt

COPY EventGenerator/config/schema.json /sample-automation-client/EventGenerator/config/schema.json
COPY EventGenerator/config/time.py /sample-automation-client/EventGenerator/config/time.py
COPY EventGenerator/Generator.py /sample-automation-client/EventGenerator/Generator.py
COPY EventGenerator/model/Event.py /sample-automation-client/EventGenerator/model/Event.py
COPY EventGenerator/model/User.py /sample-automation-client/EventGenerator/model/User.py
COPY run.py /sample-automation-client/run.py
COPY SampleClient.py /sample-automation-client/SampleClient.py

WORKDIR /sample-automation-client

CMD [ "python", "run.py" ]