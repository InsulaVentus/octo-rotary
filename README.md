Run application:
```
./sbt run -Dhttp.port=9091
```

Post:
```
curl -i -X POST 'http://localhost:9091/analytics?timestamp=42'
```
