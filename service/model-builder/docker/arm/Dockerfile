FROM jasonrandrews/tensorflow-v2

COPY ./docker/arm/requirements.txt /model-builder/requirements.txt
RUN pip install -r /model-builder/requirements.txt

COPY ./data.csv /model-builder/data.csv
COPY ./modelBuilder.py /model-builder/modelBuilder.py
COPY ./logger.py /model-builder/logger.py

WORKDIR /model-builder

USER root
CMD ["python", "-u" ,"modelBuilder.py"]
