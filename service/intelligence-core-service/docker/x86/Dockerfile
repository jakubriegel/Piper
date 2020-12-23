FROM python:3.6

COPY ./docker/x86/requirements.txt /service/requirements.txt
RUN pip install -r /service/requirements.txt

COPY ./app /service/app
COPY ./api.py /service/api.py
COPY ./model /service/model

WORKDIR /service

ARG FLASK_ENV="production"
ENV FLASK_ENV="${FLASK_ENV}" \
    PYTHONUNBUFFERED="true"

CMD python -u api.py
