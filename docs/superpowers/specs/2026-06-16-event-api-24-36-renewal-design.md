# 이벤트 API 24-36 리뉴얼 설계

## 목표

PDF의 4장 “신규 및 수정 API 상세 설명” 중 24번부터 36번까지의 API 계약을 현재 백엔드 코드에 반영한다. DB 마이그레이션은 별도로 적용된다는 전제이며, 이 작업에서는 엔티티/DTO/서비스/컨트롤러/보안 로직을 리뉴얼 계약에 맞춘다.

## 확정 DB 스키마

Java 엔티티는 아래 DB 변경사항에 맞춰 매핑한다.

```sql
alter table guiderun.event add column is_private bit default 0;
alter table guiderun.event add column expected_running_distance_km decimal(10, 2) null;
alter table guiderun.event_form add column running_distance_km decimal(10, 2) null;
alter table guiderun.event_form add column birth_date date null;
alter table guiderun.event_form add column phone_number varchar(20) null;
alter table guiderun.event_form add column status enum('APPLIED', 'CANCELED') not null default 'APPLIED';
alter table guiderun.event_form add column canceled_at datetime null;

create table guiderun.event_additional_question (
  id bigint not null auto_increment,
  event_id bigint not null,
  type enum('TEXT','SELECT') default null,
  title varchar(255) not null,
  display_order tinyint not null,
  required bit default 0,
  created_at datetime not null,
  updated_at datetime null,
  primary key (id)
);

create table guiderun.event_additional_option (
  id bigint not null auto_increment,
  question_id bigint not null,
  label varchar(255) not null,
  display_order tinyint not null,
  created_at datetime not null,
  updated_at datetime null,
  primary key (id)
);

create table guiderun.event_additional_answer (
  id bigint not null auto_increment,
  event_form_id bigint not null,
  question_id bigint not null,
  text_answer varchar(255) null,
  option_id bigint null,
  created_at datetime not null,
  updated_at datetime null,
  primary key (id)
);
```

## 설계 결정

- 신규 추가정보 엔티티는 JPA 연관관계 대신 id 컬럼 기반으로 만든다. 기존 `EventForm.eventId`, `EventForm.privateId` 스타일과 맞춘다.
- 기존 `Event.distance`는 그대로 둔다. 리뉴얼 예상 러닝 거리는 `expectedRunningDistanceKm` 필드로 새로 매핑한다.
- 신청서의 활성 상태는 `EventForm.status = APPLIED`로 본다.
- 신청 취소는 row 삭제가 아니라 `status = CANCELED`, `canceledAt = now` 저장으로 처리한다.
- 신청자 명단, 신청자 수, 내 신청 여부, 신청서 조회는 기본적으로 `APPLIED` 신청서만 대상으로 한다.
- 이벤트에 `APPLIED` 신청자가 1명 이상 있으면 `PATCH /api/event/{eventId}`에서 `additionalQuestions`가 포함된 요청을 거부한다.
- 추가질문 수정 제한은 기존 이벤트 로직 예외 스타일에 맞춰 HTTP 400으로 응답한다.
- `GET /api/event/{eventId}`는 비회원도 접근 가능하다. 토큰이 없으면 `viewer = null`, 토큰이 있으면 `viewer.isApplied`, `viewer.isOrganizer`를 계산한다.

## API별 설계

### 24. 이벤트 상세 조회

수정 대상: `GET /api/event/{eventId}`

응답에 포함할 필드:
- `eventId`, `name`, `eventType`, `eventCategory`, `recruitStatus`
- `isPrivate`
- `recruitStartDate`, `recruitEndDate`
- `organizer`
- `schedule`
- `place`
- `expectedRunningDistanceKm`
- `content`
- `additionalQuestions`
- `viewer`

비회원 요청은 `viewer: null`로 응답한다.

### 25-26. 이벤트 생성/수정

수정 대상:
- `POST /api/event`
- `PATCH /api/event/{eventId}`

요청에 추가할 필드:
- `isPrivate`
- `expectedRunningDistanceKm`
- `additionalQuestions`

추가질문 검증:
- `TEXT` 질문은 최대 1개
- `SELECT` 질문은 최대 1개
- `SELECT` 옵션은 1개 이상
- `displayOrder`는 요청 배열 순서 기준으로 저장
- 현재 요청 타입에는 required 필드가 없으므로 `required = false`로 저장

수정 정책:
- 요청에 `additionalQuestions`가 없으면 기존 추가질문은 건드리지 않는다.
- 요청에 `additionalQuestions`가 있고 `APPLIED` 신청자가 있으면 예외를 던진다.
- 요청에 `additionalQuestions`가 있고 신청자가 없으면 기존 질문/옵션/답변을 삭제하고 새 구조를 저장한다.

### 27-28. 이벤트 삭제/모집 마감

유지 대상:
- `DELETE /api/event/{eventId}`
- `PATCH /api/event/close/{eventId}`

이벤트 삭제 시 추가질문, 옵션, 답변 데이터도 함께 정리한다.

### 29-30. 러닝 거리 미입력 이벤트 조회/등록

신규 추가:
- `GET /api/event/missing-running-distance`
- `PATCH /api/event/{eventId}/running-distance`

조회 API는 현재 로그인 사용자가 주최한 종료 이벤트 중 `expectedRunningDistanceKm`가 null인 이벤트만 반환한다.

등록 API는 주최자만 호출할 수 있고, `event.expectedRunningDistanceKm`만 갱신한다.

### 31-32. 이벤트 신청서 생성/수정

수정 대상:
- `POST /api/event/{eventId}/form`
- `PATCH /api/event/{eventId}/form`

요청에 포함할 필드:
- `group`
- `partner`
- `detail`
- `competitionInfo`
- `additionalAnswers`

`COMPETITION` 이벤트는 `competitionInfo.birthDate`, `competitionInfo.phoneNumber`가 필수다.

신청서 생성 시:
- `EventForm.status = APPLIED`
- 대회 신청 정보 저장
- 추가답변 저장

신청서 수정 시:
- 기존 `APPLIED` 신청서만 수정
- 기본 신청 정보 수정
- 대회 신청 정보 수정
- 기존 추가답변 삭제 후 새 답변 저장

### 33-34. 내 신청서 조회/신청 취소

신규 추가:
- `GET /api/event/{eventId}/form`

수정 대상:
- `DELETE /api/event/{eventId}/form`

취소는 실제 삭제하지 않고 `CANCELED` 상태로 변경한다. 기존 매칭/출석 관련 운영 데이터 정리는 유지해서 취소자가 운영 목록에 남지 않도록 한다.

### 35-36. 신청자 명단/신청자 신청서 조회

수정 대상:
- `GET /api/event/{eventId}/forms`

신규 추가:
- `GET /api/event/{eventId}/forms/{userId}`

신청자 명단은 `APPLIED` 신청서만 대상으로 한다.

응답 구조:
- `summary.totalCount`
- `summary.viCount`
- `summary.guideCount`
- `groups[].runningGroup`
- `groups[].totalCount`
- `groups[].applicants[]`

신청자 신청서 상세는 주최자 또는 관리자만 조회할 수 있다.

## 보안

- 비회원 허용: `GET /api/event/{eventId}`만 해당
- 인증 필수: 나머지 25-36번 API
- 현재 `JwtAuthenticationFilter`는 Authorization 헤더가 없으면 예외를 던진다. 이를 수정해서 토큰이 없으면 필터를 통과시키고, 토큰이 제공됐는데 유효하지 않은 경우만 실패시킨다.
- `SecurityConfig`에는 숫자 eventId 상세 조회만 허용하는 method-sensitive regex matcher를 추가한다.

## 테스트 전략

- 추가질문 저장/조회 테스트
- 추가답변 저장/조회 테스트
- 신청자 존재 시 추가질문 수정 차단 테스트
- 신청 취소가 삭제가 아니라 `CANCELED` 상태 변경인지 테스트
- 러닝 거리 미입력 이벤트 조회/등록 테스트
- 신청자 명단 그룹핑 테스트
- 이벤트 상세 비회원 접근 테스트
- 로그인 후 accessToken으로 보호 API 호출 테스트

## 제외 범위

- 프론트엔드 변경은 이 작업에서 하지 않는다.
- DB 마이그레이션 SQL 적용은 이 작업에서 하지 않는다.
- 매칭 API 리뉴얼은 이 작업 범위가 아니다.
- 이벤트 모듈 전체 리팩토링은 하지 않는다.
