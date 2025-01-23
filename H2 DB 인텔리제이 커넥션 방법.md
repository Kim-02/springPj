## H2 DB커넥션 방법

## application.properties
의존성은 추가되있다는 가정
```java
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:~/local;AUTO_SERVER=TRUE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```
이렇게 설정

AUTO_SERVER=TRUE는 H2 DB를 자동으로 설정할 수 있도록 만들어주는 옵션
기본적으로 C아래 Users에 DB가 생성됨


## Intellij DB
인텔리제이 DB에 들어가서 (오른쪽 그래들 위에)

H2 DB선택 후
```
Path = ~/local.mv.db
connectiontype = Embedded
user=sa
URL = jdbc:h2~/local;AUTO_SERVER=TRUE
```

Test connection
