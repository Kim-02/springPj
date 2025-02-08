## 파일 config
###

#### docker build -t my-springboot-app .
파일 docker파일을 이용해서 컨테이너에 이미지를 추가한다. my-springboot-app이라는 이미지가 추가되게 된다.

#### docker build -f Dockerfile.nginx -t my-nginx .
nginx도커 파일을 이용해서 이미지를 추가한다.
nginx를 띄우기 위해 사용한다.

#### docker-compose up -d
compose파일을 이용해서 컨테이너를 시작한다.

이렇게 하는 이유는 이미지 각각 띄우는 고생을 덜기 위해서 따로따로 관리하지 않고

하나의 컨테이너에 관리하는 것이다.


## 발생할 수 있는 Error

#### h2database connection에러
h2데이터베이스를 이용해서 서버를 띄우는데

h2데이터베이스가 서버에 따로 올라가지 않으면 (TCP방식)

하나의 서버에 하나의 DB만 붙을 수 있기 때문에 에러가 나게 된다.

application.properties 에서 관련 내용을 확인하면 된다.

#### h2database none find 에러
말 그대로 h2데이터베이스를 찾을 수 없다는 것이다.

application.properties 에서 h2데이터베이스 저장 경로를 확인하고

docker-compose.yml 에서 정확한 Db경로를 지정하고 있는지 확인한다.

지금 파일에는 volume 으로 파일을 관리하는 방식으로 되있을 건데.

이건 의미가 없는 방식이다. tcp방식을 사용하고 있기 때문에.


## 사용한 이후 메모리 정리 방법

#### 매우 중요

wsl을 이용해서 리눅스os위에 서버를 띄우는것이기 때문에 메모리를 많이 잡아먹는다.

이걸 작업이 끝나고 사용하지 않을 때는 해제시켜야 한다.

```wsl -l -v```
현재 작업중인 컨테이너를 확인할 수 있다.

```wsl --shutdown```
wsl을 종료한다. 그러면 작업관리자에서 메모리가 줄어드는 것을 확인할 수 있다.