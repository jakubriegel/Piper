FROM python:3.6

COPY ./requirements.txt /service/requirements.txt
RUN pip install -r /service/requirements.txt

COPY ./app /service/app
COPY ./api.py /service/api.py

COPY ./model /service/model

WORKDIR /service

CMD python api.py
