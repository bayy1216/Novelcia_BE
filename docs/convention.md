# Interfaces Layer
외부로부터의 입력을 처리하는 계층
### Controller
- API 요청을 처리하는 객체
- domain의 Model을 그대로 반환할수도, DTO로 변환하여 반환할수도 있음
### xxReq 
- API 요청을 처리하는 DTO
- Swagger 문서에 적용
- not null, not blank와 같은 validation을 처리
### xxRes
- API 응답을 처리하는 DTO
- Swagger 문서에 적용
- DOMAIN 객체를 변환이 필요할시 사용

# Domain Layer
비즈니스 로직을 처리하는 계층
### Entity
- prefix, suffix 없이 도메인 객체를 표현
### Service
- 비즈니스 로직을 처리하는 객체
- Port를 통해 외부와 통신하거나, Port와의 연산을 추상화한 UseCase를 호출
### UseCase
- Service에서 비즈니스 로직을 추상화한 객체
- 무상태를 가져야하고 Service에서의 로직의 추상화수준을 맞춰야 할때, UseCase를 사용
### Port
- 외부와의 통신을 추상화한 인터페이스
- Reader
  - 외부에서 데이터를 읽어오는 책임을 가진다
  - 멱등성을 보장하여야 한다
- Writer
  - 외부로 데이터를 전송하는 책임을 가진다
  - 멱등성이 보장되지 않아도 된다
  - JPA의 영속성을 보장하지 않는 메소드를 가질 수 있다

# Infrastructure Layer
- 외부와의 통신을 처리하는 계층
- JPA를 사용할시, data-jpa, querydsl, jdbc-template와 같은 부분을 Port를 구현하는 방식으로 추상화