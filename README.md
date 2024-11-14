## 실습 목표
+ 스프링 시큐리티 6 프레임워크를 활용하여 JWT 기반의 인증/인가를 구현.
+ 서버는 API 서버 형태로 구축. (웹 페이지를 응답하는 것이 아닌 API 클라이언트 요청을 통해 데이터 응답만 확인함)
<br>


---
 

## 구현
+ 인증 : 로그인
+ 인가 : JWT를 통한 경로별 접근 권한
+ 회원가입
<br>

---

### 버전 및 의존성
 + Spring Boot 3.3.5
 + Security 6.3.4
 + Spring Data JPA - MySQL
 + jjwt-api:0.12.3
 + jjwt-impl:0.12.3
 + jjwt-jackson:0.12.3
