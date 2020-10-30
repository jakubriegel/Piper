FROM python:3.6

ADD . /intelligence-core-service
WORKDIR /intelligence-core-service
RUN pip install -r requirements.txt

CMD python api.py