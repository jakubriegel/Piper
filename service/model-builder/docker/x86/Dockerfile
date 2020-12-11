FROM python:3.7

COPY ./docker/x86/requirements.txt /model-builder/requirements.txt
RUN pip install -r /model-builder/requirements.txt

COPY ./data.csv /model-builder/data.csv
COPY ./modelBuilder.py /model-builder/modelBuilder.py
COPY ./logger.py /model-builder/logger.py

WORKDIR /model-builder

CMD python -u modelBuilder.py
