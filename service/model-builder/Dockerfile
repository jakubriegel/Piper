FROM python:3.6

COPY ./requirements.txt /model-builder/requirements.txt
RUN pip install -r /model-builder/requirements.txt

COPY ./modelBuilder.py /model-builder/modelBuilder.py

WORKDIR /model-builder

CMD python modelBuilder.py
