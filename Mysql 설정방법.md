## Mysql

 - 8.4.4 버전설치
 - username root
 - password 1234
 - 나머지 설정은 건들지 말고 인텔리제이에 DB커넥션

## CREATE database
 - 쿼리를 날림
 - CREATE DATABASE local_db;
 - 확인
 - SHOW DATABASES;
 - 여기에 local_db가 생성되었는지 확인

## insert
 - DB에 보면 user_like 테이블이 있음
 - 쿼리 실행
    ```
    UPDATE user_like
    SET liked =0
    WHERE author = 'test';
   ```