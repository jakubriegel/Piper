# Piper
Piper will be an AI based system, that will be able to propose automated routines suggestions based on real life events.

# Model building through Kafka
If you want trigger model build send kafka event on topic `UserDataSamplesTopic` with payload like this:
`{"path": "path/to/data.csv"}

To send event manually you can enter kafka container console (using `docker exec` command) and run this: 
```kafka-console-producer --broker-list localhost:9092 --topic UserData```
And then paste above payload and hit enter.

To debug event you can listen on it using command as above: 
```kafka-console-consumer --bootstrap-server localhost:9092 --topic UserData```

IMPORTANT Note: Kafka in this configuration may <b>NOT</b> be visible outside docker network!!!