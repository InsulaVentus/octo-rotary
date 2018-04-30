## Event Store

Store and retrieve events from an in-memory data cluster

* Java 1.8
* Playframework 2.6.13
* Hazelcast 3.9.3

To run the application:
```
./sbt run -Dhttp.port=9000
```

Add analytics:
```
curl -i -X POST 'http://localhost:9000/analytics?timestamp=1522544400000&user=mary&click'
```

Get analytics:
```
curl -i 'http://localhost:9000/analytics?timestamp=1522544400000'
```
