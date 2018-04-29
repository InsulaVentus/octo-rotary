Run application:
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
