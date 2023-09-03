# sns-refactoring
#### "슈퍼챌린지 소프트웨어 해커톤"에서 진행한 프로젝트 일부 기능을 tdd 방식으로 리팩토링한 프로젝트입니다.

### 주요 기능
- **공통 포맷 응답 처리**
- **JWT 회원가입/로그인 구현**
- **알람 기능 구현**
- **조회 속도 향상을 위한 Index 적용**
- **댓글 기능 구현**
- **좋아요 기능 구현**

### 문제점 개선 및 성능 최적화
- **token 인증시 user를 조회하고, 그 이후 또 user를 조회하는 구조**

- **매 api요청마다 user 조회**

- **alarm list api를 호출해야만 갱신되는 알람 목록**

- **query 최적화 여부**

### 해야할 일
- **알람 생성 비동기 처리**

### ERD
![202308261908033](https://github.com/ksw13/sns-refactoring/assets/121210456/e1780ee7-80fa-4c38-b535-9a6c63e93c59)


### Architecture
![20230826190802](https://github.com/ksw13/sns-refactoring/assets/121210456/e82226e9-cc08-479a-842d-f7756f7a65fe)