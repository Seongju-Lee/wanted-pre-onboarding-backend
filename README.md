# wanted-pre-onboarding-backend

<h2> 👩‍💻 지원자
--
<h3> 이성주 </h3>

<h2> ✅ 애플리케이션 실행 방법
--
애플리케이션 실행 방법에는 두 가지가 있습니다.  
- **해당 소스코드를 docker-compose를 이용한 환경구성 및 실행**
- **클라우드 환경에 배포된 API 호출**

<h3>해당 소스코드를 docker-compose를 이용한 환경구성 및 실행</h3>

1. docker-compose.yml 파일의 환경변수를 지정합니다.  
   이 환경 변수는 애플리케이션과 데이터베이스 설정에 사용됩니다.
   - **${docker.database.name}:** MySQL 이미지의 데이터베이스명
   - **${docker.database.root-password}:** MySQL 이미지의 데이터베이스에 접근하기 위한 root계정의 비밀번호
   - **${docker.database.user}:** 데이터베이스에 접근하기 위한 사용자
   - **${docker.database.password}:** 데이터베이스에 접근하기 위한 비밀번호
   - **${jwt.expiration-key}:** JWT 유효시간
   - **${jwt.issuer}:** 토큰 발급자
   - **${jwt.secret-key}:** JWT 발급을 위한 secret key
   

2. 해당 프로젝트의 root directory로 이동합니다.  


3. 터미널에 ```docker-compose up``` 또는 ```docker-compose up -d``` 를 입력하여 컨테이너를 작동시킵니다.
   - ```docker-compose up```:  컨테이너가 터미널 세션에서 실행되며 로그가 터미널에 실시간으로 출력됩니다.
   - ```docker-compose up -d```: 컨테이너가 백그라운드에서 실행됩니다.


4. 실행 확인을 위해 ```docker ps -a``` 명령어를 실행시킨다.


5. ```http://localhost:80```을 통해 API를 호출할 수 있습니다. (port번호 생략 가능)  


6. 데이터베이스는 ```localhost:3306```을 통해 접속할 수 있습니다.


<h3>클라우드 환경에 배포된 API</h3>
1. ```34.64.245.242```을 통해 API를 호출할 수 있습니다.  
다양한 API에 대해서는 API명세 또는 시연영상을 통해 확인하실 수 있습니다.


<h2> 📌 데이터베이스 테이블 구조
--


<h2> 📚API 명세
--
