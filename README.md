# wanted-pre-onboarding-backend

## 👩‍💻 지원자

### 이성주

## ✅ 애플리케이션 실행 방법

애플리케이션 실행 방법에는 두 가지가 있습니다.

### 해당 소스코드를 docker-compose를 이용한 환경구성 및 실행

1. `docker-compose.yml` 파일의 환경변수를 지정합니다. 이 환경 변수는 애플리케이션과 데이터베이스 설정에 사용됩니다.
   - **${docker.database.name}:** MySQL 이미지의 데이터베이스명
   - **${docker.database.root-password}:** MySQL 이미지의 데이터베이스에 접근하기 위한 root 계정의 비밀번호
   - **${docker.database.user}:** 데이터베이스에 접근하기 위한 사용자
   - **${docker.database.password}:** 데이터베이스에 접근하기 위한 비밀번호
   - **${jwt.expiration-key}:** JWT 유효시간
   - **${jwt.issuer}:** 토큰 발급자
   - **${jwt.secret-key}:** JWT 발급을 위한 secret key

2. 해당 프로젝트의 루트 디렉토리로 이동합니다.

3. 터미널에 다음 명령어를 입력하여 컨테이너를 실행합니다.
   - `docker-compose up`: 컨테이너가 터미널 세션에서 실행되며 로그가 터미널에 실시간으로 출력됩니다.
   - `docker-compose up -d`: 컨테이너가 백그라운드에서 실행됩니다.

4. 실행 확인을 위해 다음 명령어를 실행하여 실행 중인 컨테이너를 확인합니다.
   ```
   docker ps -a
   ```

5. `http://localhost:80`을 통해 API를 호출할 수 있습니다. (포트번호 생략 가능)

6. 데이터베이스는 `localhost:3306`을 통해 접속할 수 있습니다.

### 클라우드 환경에 배포된 API

1. `34.64.245.242`을 통해 API를 호출할 수 있습니다. 다양한 API에 대해서는 [API 명세](https://documenter.getpostman.com/view/21381528/2s9Xy3rqmx) 또는 [시연 영상](https://youtu.be/5dSjj8lDnUM)을 통해 확인하실 수 있습니다.


## 📌 데이터베이스 테이블 구조
<img src="https://github.com/Seongju-Lee/wanted-pre-onboarding-backend/assets/67941526/2d6fbaf9-8dfc-4f33-97a6-d3f2f48beb73" alt="diagram" width="300">

### User 테이블

- User 테이블은 사용자 정보를 저장하는 엔티티입니다.
- `userId`: User 테이블의 기본 키로서, 각 사용자의 고유한 식별자입니다.
- `email`: 사용자의 이메일 주소를 저장하는 컬럼으로, 고유한 값을 가지고 있어야 합니다.
- `password`: 사용자의 비밀번호를 저장하는 컬럼입니다.

### Post 테이블

- Post 테이블은 게시글 정보를 저장하는 엔티티입니다.
- `postId`: Post 테이블의 기본 키로서, 각 게시글의 고유한 식별자입니다.
- `user_id`: User와의 관계를 나타내는 외래 키 컬럼으로, 어떤 사용자가 해당 게시글을 작성했는지를 나타냅니다.
- `title`: 게시글의 제목을 저장하는 컬럼입니다.
- `content`: 게시글의 내용을 저장하는 컬럼입니다.

### 관계

- User와 Post는 다대일(Many-to-One) 관계입니다. 하나의 사용자는 여러 개의 게시글을 작성할 수 있지만, 각 게시글은 하나의 사용자에 의해 작성되었습니다.
- User 테이블의 `userId` 컬럼과 Post 테이블의 `user_id` 컬럼 간에 외래 키 관계가 형성됩니다.
- FetchType은 LAZY로 설정되어 있어 게시글을 조회할 때 사용자 정보를 필요로 할 때만 가져옵니다.
- Post 엔티티에는 User 엔티티에 대한 참조(`private User user;`)가 존재하며, 이를 통해 두 엔티티 간의 관계가 맺어집니다.

이러한 테이블 관계는 JPA 어노테이션을 통해 설정되었으며, `User` 엔티티와 `Post` 엔티티 간에 다대일 관계가 형성되어 있습니다.

## 구현한 API의 동작을 촬영한 데모 영상 링크
[데모 영상 보러가기](https://youtu.be/5dSjj8lDnUM)

## 구현 방법 및 이유에 대한 간략한 설명

### 아키텍처 설계
<img width="543" src="https://github.com/Seongju-Lee/wanted-pre-onboarding-backend/assets/67941526/6fafbe5f-535c-4efc-b2fd-a9a50b36e96d">  

- GCP VM 인스턴스를 선택한 이유는 필요에 따라 수평적 스케일링을 할 수 있는 환경을 구축하기 위함이었습니다.

- 애플리케이션과 데이터베이스는 서로 다른 특성을 가지기 때문에 분리하고자 GCP Cloud SQL을 통해 DB 서버를 구축하였습니다. 이를 통해 DB 서버의 자원을 데이터 처리에 집중시키며, 애플리케이션 서버와는 독립적으로 확장하고 유지할 수 있을 것으로 판단했습니다.

- 컨테이너를 활용하여 배포함으로써 애플리케이션과 필요한 라이브러리, 설정 등을 패키징하여 한 번에 배포할 수 있었습니다. 이를 통해 개발 환경과 운영 환경 간의 일관성을 유지할 수 있었습니다.
### 패키지 구조 설계

### Security 설계

### 테스트코드

### 예외처리

### DTO 설계

## 📚 API 명세(request/response 포함)

[API 명세 바로가기](https://documenter.getpostman.com/view/21381528/2s9Xy3rqmx)

<img src="https://github.com/Seongju-Lee/wanted-pre-onboarding-backend/assets/67941526/56aca9ff-6882-46fc-9186-8aaa0e0c4d76" alt="API 명세 이미지" width="370">

위 명세 페이지에서 각 API 우측에 있는 드롭다운을 통해 **각 API별 예외 상황에 대한 요청과 응답도 확인 가능**합니다.

### 회원
- POST /api/users/signup (회원가입)
- POST /api/users/login (로그인)

### 게시글
- POST /api/posts (게시글 생성)
- GET /api/posts?page={pageNumber} (게시글 목록 조회)
- GET /api/posts/{postId} (특정 게시글 조회)
- PATCH /api/posts/{postId} (게시글 수정)
- DELETE /api/posts/{postId} (게시글 삭제)
