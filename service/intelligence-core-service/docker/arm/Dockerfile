FROM jasonrandrews/tensorflow-v2

COPY ./docker/arm/requirements.txt /service/requirements.txt
RUN pip install -r /service/requirements.txt

COPY ./app /service/app
COPY ./api.py /service/api.py
COPY ./model /service/model

WORKDIR /service

ARG FLASK_ENV="production"
ENV FLASK_ENV="${FLASK_ENV}" \
    PYTHONUNBUFFERED="true"

USER root
CMD ["python", "-u" ,"api.py"]
