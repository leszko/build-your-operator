FROM python:3.7
RUN pip install kopf
ADD operator.py /src/operator.py
CMD kopf run /src/operator.py --verbose