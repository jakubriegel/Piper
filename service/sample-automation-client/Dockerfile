FROM python:3.9

COPY ./requirements.txt /sample-automation-client/requirements.txt
RUN pip install -r /sample-automation-client/requirements.txt

COPY ./schema.json /sample-automation-client/schema.json
COPY ./client.py /sample-automation-client/client.py

WORKDIR /sample-automation-client

CMD [ "python", "client.py" ]