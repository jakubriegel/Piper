# model-builder
## Model building through Kafka
If you want trigger model build send kafka event on topic `UserDataSamplesTopic` with payload like this:
`{"path": "path/to/data.csv"}`.

By default there is one dataset soo you can send this payload for testing purpose: `{"path": "data.csv"}`.

Models are saved in `/model-builder/models` folder.

To send event manually you can enter kafka container console (using `docker exec` command) and run this: 
```kafka-console-producer --broker-list localhost:9092 --topic UserData```
And then paste above payload and hit enter.

To debug event you can listen on it using command as above: 
```kafka-console-consumer --bootstrap-server localhost:9092 --topic UserData```

IMPORTANT Note: Kafka in this configuration may <b>NOT</b> be visible outside docker network!!!
