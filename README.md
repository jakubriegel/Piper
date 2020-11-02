# Piper
Piper will be an AI based system, that will be able to propose automated routines suggestions based on real life events.

# Model building through Kafka
If you want trigger model build send kafka event on topic `UserDataSamplesTopic` with payload like this:
`{"path": "path/to/data.csv"}

To send event manually you can enter kafka container console (using `docker exec` command) and run this: 
```kafka-console-producer.sh --broker-list localhost:9092 --topic UserDataSamplesTopic```
And then paste above payload and hit enter.