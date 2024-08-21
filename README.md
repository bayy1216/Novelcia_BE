# Novelcia
Spring Boot로 만드는 소설 플랫폼

## 개발환경
- `Kotlin`  1.9.24
- `Spring Boot` 3.2.4
- `Gradle` 
- `JPA`, `QueryDSL` 1.0.10
- `Spring Cloud Config` 2023.0.3
- `MySQL` 8.0, `Flyway`, `Redis` 7.2.3
- `Docker`, `Docker Compose`, `Docker Swarm`, `Github Actions`
- `Azure`

## 기능
- User
- Auth
- Novel
- Novel Episode
- Novel Favorite

## 기타
- 프로젝트 구조 : [convention.md](./docs/convention.md)
- kotlinDSL 적용 : [TxManager](./src/main/kotlin/com/reditus/novelcia/global/util/TxUtils.kt)
- 조회수 증가 WriteBack 적용 : [WriteBackManager](./src/main/kotlin/com/reditus/novelcia/domain/common/WriteBackManager.kt), [NovelViewWriteBackManager](./src/main/kotlin/com/reditus/novelcia/domain/novel/application/NovelViewWriteBackManager.kt)
- Novel 랭킹 스코어링 캐싱 적용 : [NovelQueryService](./src/main/kotlin/com/reditus/novelcia/domain/novel/application/NovelQueryService.kt)
- 유저 포인트 멱등키 적용 : [UserService](./src/main/kotlin/com/reditus/novelcia/domain/user/UserService.kt)
