## application.properties

```
spring.datasource.url=jdbc:mysql://localhost:3310/logs
spring.datasource.username=logstash
spring.datasource.password=1234
```
3310은 도커에 띄운 mysql 포트 넘버를 적고 logs는 안에 db를 적는다.

권한이 있는 user정보를 적는다.


```
spring.elasticsearch.uris=http://localhost:9200
spring.elasticsearch.connection-timeout=5s
spring.elasticsearch.socket-timeout=10s
logging.level.org.springframework.data.elasticsearch=INFO
```

elastic 설정 따로 건들건 없다.

## mysql-connector-java.jar

따로 건들 필요없다. 프로젝트 파일 내에 있으면 됌

## docker-compose.yml

특별히 건들건 없다. 

이렇게 하면 이제 

크롬 확장 프로그램에서 multi elastic head 를 추가하고

http://localhost:9200

logstash

1234

이렇게 입력하면 접속할 수 있다.