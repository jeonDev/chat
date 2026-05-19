# Netty Chatting Server (PostgreSQL)
- PostgreSQL / Netty 사용을 위한 프로젝트
- 모든 데이터 처리 건은 PostgreSQL로 해결해보자 프로젝트
- Netty 기반 채팅 서버 구현

---

## 구성

- RDB
  - 고객 정보 / 채팅방 관리
  - PostgreSQL RDB 기능 사용

- NoSQL
  - 채팅 메시지
  - ~~MongoDB~~ → PostgreSQL JSONB로 버텨보기

- Queue / Messaging
  - 이벤트 전파
  - ~~Kafka~~ → PostgreSQL LISTEN / NOTIFY로 밀어붙이기

---

> 완성을 목표로,,
