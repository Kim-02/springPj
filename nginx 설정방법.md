## nginx

 - nginx 1.26.3버전 설치 후 압축 해제
 - nginx.conf 파일은 vscode 같은 편집기에서 편집해야함. 메모장x
 - \nginx-1.26.3\conf\nginx.conf
  ```
    #access_log  logs/access.log  main;
	upstream backend_servers {
		server localhost:8081;
		server localhost:8082;
		server localhost:8083;
	}
    sendfile        on;
    #tcp_nopush     on;

    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;

    server {
        listen       80;
        server_name  localhost;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

         location / {
	        proxy_pass http://backend_servers;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

  ```
  이렇게 수정
  
## 명령어
cmd

 - start nginx
 - nginx -s stop