FROM python:3.6

ADD . /model-builder-consumer
WORKDIR /model-builder-consumer
RUN pip install -r requirements.txt

CMD python modelBuilder.py