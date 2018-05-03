## Event Store

Store and retrieve events from an in-memory data cluster. Made with:

* Java 1.8
* Playframework 2.6.13
* Hazelcast 3.9.3

### To run the application:
```
./sbt run
```
The application will be available on port 9000. To use a different port, run:
```
./sbt run -Dhttp.port={port}
``` 

### Application endpoints:
```
POST /analytics?timestamp={millis_since_epoch}&user={username}&{click|impression}
```
and
```
GET /analytics?timestamp={millis_since_epoch}
```

### Examples:
`POST` request:
```
$ curl -X POST 'http://localhost:9000/analytics?timestamp=1522544400000&user=mary&click'
```

`GET` request:
```
$ curl 'http://localhost:9000/analytics?timestamp=1522544400000'
unique_users,1
clicks,1
impressions,0
```
