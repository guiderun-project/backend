# 이벤트 API 24-36 리뉴얼 구현 계획

> **작업자 필수 지침:** 이 계획을 실행할 때는 작업 단위별로 체크박스를 갱신한다. 구현은 작은 단위로 나누고, 각 단계마다 컴파일 또는 테스트로 검증한다.

**목표:** PDF 명세의 이벤트 API 24번부터 36번까지를 현재 백엔드 코드에 반영한다.

**구조:** 추가질문, 옵션, 답변은 신규 엔티티와 repository로 분리한다. 기존 route가 있는 API는 컨트롤러 경로를 유지하되, 리뉴얼 응답/요청 DTO와 서비스 로직을 새 계약에 맞춘다.

**기술 스택:** Java, Spring Boot, Spring MVC, Spring Security, Spring Data JPA, Gradle.

---

## 변경 파일 구조

생성:
- `run/src/main/java/com/guide/run/event/entity/type/AdditionalQuestionType.java`
- `run/src/main/java/com/guide/run/event/entity/type/EventFormStatus.java`
- `run/src/main/java/com/guide/run/event/entity/EventAdditionalQuestion.java`
- `run/src/main/java/com/guide/run/event/entity/EventAdditionalOption.java`
- `run/src/main/java/com/guide/run/event/entity/EventAdditionalAnswer.java`
- `run/src/main/java/com/guide/run/event/entity/repository/EventAdditionalQuestionRepository.java`
- `run/src/main/java/com/guide/run/event/entity/repository/EventAdditionalOptionRepository.java`
- `run/src/main/java/com/guide/run/event/entity/repository/EventAdditionalAnswerRepository.java`
- `run/src/main/java/com/guide/run/event/entity/dto/request/EventApplyRequest.java`
- `run/src/main/java/com/guide/run/event/entity/dto/request/EventRunningDistancePatchRequest.java`
- `run/src/main/java/com/guide/run/event/entity/dto/response/EventDetailResponse.java`
- `run/src/main/java/com/guide/run/event/entity/dto/response/EventRunningDistancePatchResponse.java`
- `run/src/main/java/com/guide/run/event/entity/dto/response/MissingRunningDistanceGetResponse.java`
- `run/src/main/java/com/guide/run/event/entity/dto/response/form/MyEventApplyGetResponse.java`
- `run/src/main/java/com/guide/run/event/entity/dto/response/form/EventApplicantListResponse.java`
- `run/src/main/java/com/guide/run/event/entity/dto/response/form/EventApplicantFormResponse.java`
- `run/src/main/java/com/guide/run/event/service/EventAdditionalInfoService.java`
- `run/src/main/java/com/guide/run/global/exception/event/logic/CannotModifyAdditionalQuestionsException.java`
- `run/src/test/java/com/guide/run/event/service/EventAdditionalInfoServiceTest.java`
- `run/src/test/java/com/guide/run/event/service/EventRenewalServiceTest.java`

수정:
- `run/src/main/java/com/guide/run/event/entity/Event.java`
- `run/src/main/java/com/guide/run/event/entity/EventForm.java`
- `run/src/main/java/com/guide/run/event/entity/dto/request/EventCreateRequest.java`
- `run/src/main/java/com/guide/run/event/entity/repository/EventRepository.java`
- `run/src/main/java/com/guide/run/event/entity/repository/EventFormRepository.java`
- `run/src/main/java/com/guide/run/event/controller/EventController.java`
- `run/src/main/java/com/guide/run/event/controller/EventFormController.java`
- `run/src/main/java/com/guide/run/event/controller/EventAttendanceController.java`
- `run/src/main/java/com/guide/run/event/service/EventService.java`
- `run/src/main/java/com/guide/run/event/service/EventFormService.java`
- `run/src/main/java/com/guide/run/event/service/EventAttendService.java`
- `run/src/main/java/com/guide/run/global/jwt/JwtAuthenticationFilter.java`
- `run/src/main/java/com/guide/run/global/security/config/SecurityConfig.java`
- `run/src/main/java/com/guide/run/global/exception/event/EventLogicExceptionAdvice.java`
- `run/src/main/resources/i18n/exception_ko.yml`

---

## 작업 1. 리뉴얼 엔티티 모델 추가

**파일:**
- 생성: `run/src/main/java/com/guide/run/event/entity/type/AdditionalQuestionType.java`
- 생성: `run/src/main/java/com/guide/run/event/entity/type/EventFormStatus.java`
- 생성: `run/src/main/java/com/guide/run/event/entity/EventAdditionalQuestion.java`
- 생성: `run/src/main/java/com/guide/run/event/entity/EventAdditionalOption.java`
- 생성: `run/src/main/java/com/guide/run/event/entity/EventAdditionalAnswer.java`
- 수정: `run/src/main/java/com/guide/run/event/entity/Event.java`
- 수정: `run/src/main/java/com/guide/run/event/entity/EventForm.java`

- [ ] **1단계: enum 추가**

```java
package com.guide.run.event.entity.type;

public enum AdditionalQuestionType {
    TEXT,
    SELECT
}
```

```java
package com.guide.run.event.entity.type;

public enum EventFormStatus {
    APPLIED,
    CANCELED
}
```

- [ ] **2단계: 추가질문 엔티티 추가**

`EventAdditionalQuestion`:

```java
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAdditionalQuestion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventId;

    @Enumerated(EnumType.STRING)
    private AdditionalQuestionType type;

    private String title;
    private Integer displayOrder;
    private boolean required;
}
```

`EventAdditionalOption`:

```java
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAdditionalOption extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;
    private String label;
    private Integer displayOrder;
}
```

`EventAdditionalAnswer`:

```java
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAdditionalAnswer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long eventFormId;
    private Long questionId;
    private String textAnswer;
    private Long optionId;
}
```

- [ ] **3단계: 기존 엔티티 필드 추가**

`Event`에 추가:

```java
private boolean isPrivate;
private BigDecimal expectedRunningDistanceKm;
```

`EventForm`에 추가:

```java
private BigDecimal runningDistanceKm;
private LocalDate birthDate;
private String phoneNumber;

@Enumerated(EnumType.STRING)
private EventFormStatus status;

private LocalDateTime canceledAt;
```

`EventForm`에 메서드 추가:

```java
public void cancel(LocalDateTime canceledAt) {
    this.status = EventFormStatus.CANCELED;
    this.canceledAt = canceledAt;
}

public void updateCompetitionInfo(LocalDate birthDate, String phoneNumber) {
    this.birthDate = birthDate;
    this.phoneNumber = phoneNumber;
}
```

- [ ] **4단계: 컴파일**

```bash
cd run && ./gradlew compileJava
```

기대 결과: import 누락을 제외하면 컴파일 성공.

---

## 작업 2. repository 추가

**파일:**
- 생성: `run/src/main/java/com/guide/run/event/entity/repository/EventAdditionalQuestionRepository.java`
- 생성: `run/src/main/java/com/guide/run/event/entity/repository/EventAdditionalOptionRepository.java`
- 생성: `run/src/main/java/com/guide/run/event/entity/repository/EventAdditionalAnswerRepository.java`
- 수정: `run/src/main/java/com/guide/run/event/entity/repository/EventFormRepository.java`
- 수정: `run/src/main/java/com/guide/run/event/entity/repository/EventRepository.java`

- [ ] **1단계: 추가질문 repository 추가**

```java
public interface EventAdditionalQuestionRepository extends JpaRepository<EventAdditionalQuestion, Long> {
    List<EventAdditionalQuestion> findAllByEventIdOrderByDisplayOrderAsc(Long eventId);
    void deleteAllByEventId(Long eventId);
}
```

```java
public interface EventAdditionalOptionRepository extends JpaRepository<EventAdditionalOption, Long> {
    List<EventAdditionalOption> findAllByQuestionIdOrderByDisplayOrderAsc(Long questionId);
    List<EventAdditionalOption> findAllByQuestionIdInOrderByDisplayOrderAsc(List<Long> questionIds);
    void deleteAllByQuestionIdIn(List<Long> questionIds);
}
```

```java
public interface EventAdditionalAnswerRepository extends JpaRepository<EventAdditionalAnswer, Long> {
    List<EventAdditionalAnswer> findAllByEventFormId(Long eventFormId);
    void deleteAllByEventFormId(Long eventFormId);
    void deleteAllByQuestionIdIn(List<Long> questionIds);
}
```

- [ ] **2단계: 활성 신청서 조회 메서드 추가**

`EventFormRepository`에 추가:

```java
EventForm findByEventIdAndPrivateIdAndStatus(Long eventId, String privateId, EventFormStatus status);
List<EventForm> findAllByEventIdAndStatus(Long eventId, EventFormStatus status);
long countByEventIdAndStatus(Long eventId, EventFormStatus status);
long countByEventIdAndTypeAndStatus(Long eventId, UserType type, EventFormStatus status);
```

- [ ] **3단계: 러닝 거리 미입력 이벤트 조회 메서드 추가**

`EventRepository`에 추가:

```java
List<Event> findAllByOrganizerAndEndTimeBeforeAndExpectedRunningDistanceKmIsNull(
    String organizer,
    LocalDateTime now
);
```

- [ ] **4단계: 컴파일**

```bash
cd run && ./gradlew compileJava
```

기대 결과: 컴파일 성공.

---

## 작업 3. 요청/응답 DTO 추가

**파일:**
- 수정: `run/src/main/java/com/guide/run/event/entity/dto/request/EventCreateRequest.java`
- 생성: `run/src/main/java/com/guide/run/event/entity/dto/request/EventApplyRequest.java`
- 생성: `run/src/main/java/com/guide/run/event/entity/dto/request/EventRunningDistancePatchRequest.java`
- 생성: 리뉴얼 응답 DTO 파일들

- [ ] **1단계: 이벤트 생성/수정 요청 확장**

`EventCreateRequest`에 추가:

```java
private Boolean isPrivate;
private BigDecimal expectedRunningDistanceKm;
private List<AdditionalQuestionRequest> additionalQuestions;
```

중첩 클래스 추가:

```java
@Getter
@AllArgsConstructor
@NoArgsConstructor
public static class AdditionalQuestionRequest {
    private AdditionalQuestionType type;
    private String title;
    private List<String> options;
}
```

- [ ] **2단계: 신청서 요청 DTO 추가**

`EventApplyRequest`:

```java
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventApplyRequest {
    private String group;
    private String partner;
    private String detail;
    private CompetitionApplicationInfo competitionInfo;
    private List<AdditionalAnswerRequest> additionalAnswers;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CompetitionApplicationInfo {
        private LocalDate birthDate;
        private String phoneNumber;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdditionalAnswerRequest {
        private Long questionId;
        private AdditionalQuestionType type;
        private String answerText;
        private Long selectedOptionId;
    }
}
```

- [ ] **3단계: 러닝 거리 요청 DTO 추가**

```java
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EventRunningDistancePatchRequest {
    private BigDecimal expectedRunningDistanceKm;
}
```

- [ ] **4단계: 응답 DTO 추가**

아래 DTO를 PDF 필드명 기준으로 만든다.

- `EventDetailResponse`
- `MissingRunningDistanceGetResponse`
- `EventRunningDistancePatchResponse`
- `MyEventApplyGetResponse`
- `EventApplicantListResponse`
- `EventApplicantFormResponse`

거리 타입은 `BigDecimal`, 날짜 타입은 `LocalDate`를 사용한다.

- [ ] **5단계: 컴파일**

```bash
cd run && ./gradlew compileJava
```

기대 결과: 컴파일 성공.

---

## 작업 4. 추가정보 서비스 구현

**파일:**
- 생성: `run/src/main/java/com/guide/run/event/service/EventAdditionalInfoService.java`
- 테스트: `run/src/test/java/com/guide/run/event/service/EventAdditionalInfoServiceTest.java`

- [ ] **1단계: 테스트 작성**

테스트 케이스:
- TEXT 질문 1개 저장 성공
- SELECT 질문 1개와 옵션 저장 성공
- TEXT 질문 2개 요청 시 실패
- SELECT 질문 2개 요청 시 실패
- SELECT 옵션이 빈 배열이면 실패
- displayOrder 순서대로 질문 조회
- SELECT 답변의 optionId가 해당 질문의 옵션이 아니면 실패

- [ ] **2단계: 서비스 메서드 구현**

```java
public void replaceQuestions(Long eventId, List<EventCreateRequest.AdditionalQuestionRequest> requests)
public List<EventDetailResponse.AdditionalQuestion> getQuestions(Long eventId)
public void replaceAnswers(Long eventFormId, List<EventApplyRequest.AdditionalAnswerRequest> requests)
public List<MyEventApplyGetResponse.AdditionalAnswerDetail> getAnswerDetails(Long eventFormId)
public void deleteAllForEvent(Long eventId)
```

구현 규칙:
- `replaceQuestions`는 기존 답변, 옵션, 질문을 삭제한 뒤 새 구조를 저장한다.
- `replaceAnswers`는 기존 답변을 삭제한 뒤 새 답변을 저장한다.
- 요청 답변 타입과 저장된 질문 타입이 다르면 실패한다.
- SELECT 답변의 `selectedOptionId`는 해당 질문의 option이어야 한다.

- [ ] **3단계: 테스트 실행**

```bash
cd run && ./gradlew test --tests "*EventAdditionalInfoServiceTest"
```

기대 결과: 통과.

---

## 작업 5. 이벤트 생성/수정/상세/삭제 수정

**파일:**
- 수정: `run/src/main/java/com/guide/run/event/controller/EventController.java`
- 수정: `run/src/main/java/com/guide/run/event/service/EventService.java`
- 생성: `run/src/main/java/com/guide/run/global/exception/event/logic/CannotModifyAdditionalQuestionsException.java`
- 수정: `run/src/main/java/com/guide/run/global/exception/event/EventLogicExceptionAdvice.java`
- 수정: `run/src/main/resources/i18n/exception_ko.yml`
- 테스트: `run/src/test/java/com/guide/run/event/service/EventRenewalServiceTest.java`

- [ ] **1단계: 예외 추가**

```java
public class CannotModifyAdditionalQuestionsException extends RuntimeException {
    public CannotModifyAdditionalQuestionsException() {}
}
```

`exception_ko.yml` 추가:

```yaml
CannotModifyAdditionalQuestions:
  code: "2209"
  msg: "신청자가 있는 이벤트는 추가정보를 수정할 수 없습니다."
```

`EventLogicExceptionAdvice`에 HTTP 400 handler를 추가한다.

- [ ] **2단계: 이벤트 생성 수정**

`eventCreate`에서 처리:
- `isPrivate`가 null이면 false 저장
- `expectedRunningDistanceKm` 저장
- 이벤트 저장 후 `additionalInfoService.replaceQuestions` 호출

- [ ] **3단계: 이벤트 수정 정책 반영**

요청에 `additionalQuestions`가 포함된 경우:

```java
long appliedCount = eventFormRepository.countByEventIdAndStatus(eventId, EventFormStatus.APPLIED);
if (appliedCount > 0) {
    throw new CannotModifyAdditionalQuestionsException();
}
additionalInfoService.replaceQuestions(eventId, request.getAdditionalQuestions());
```

일반 이벤트 필드는 신청자 수와 무관하게 수정한다.

- [ ] **4단계: 이벤트 상세 응답 변경**

컨트롤러에서 강제 토큰 추출 대신 선택 토큰 추출을 사용한다.

```java
String privateId = jwtProvider.tryExtractUserId(request);
```

서비스는 `privateId == null`이면 `viewer = null`로 응답한다.

- [ ] **5단계: 이벤트 삭제 시 추가정보 정리**

이벤트 삭제 전에 호출:

```java
additionalInfoService.deleteAllForEvent(eventId);
```

- [ ] **6단계: 테스트 실행**

```bash
cd run && ./gradlew test --tests "*EventRenewalServiceTest"
```

기대 결과: 통과.

---

## 작업 6. 러닝 거리 미입력 이벤트 API 추가

**파일:**
- 수정: `run/src/main/java/com/guide/run/event/controller/EventController.java`
- 수정: `run/src/main/java/com/guide/run/event/service/EventService.java`

- [ ] **1단계: 컨트롤러 메서드 추가**

```java
@GetMapping("/missing-running-distance")
public ResponseEntity<MissingRunningDistanceGetResponse> getMissingRunningDistance(HttpServletRequest request)
```

```java
@PatchMapping("/{eventId}/running-distance")
public ResponseEntity<EventRunningDistancePatchResponse> patchRunningDistance(
    @PathVariable Long eventId,
    @RequestBody EventRunningDistancePatchRequest requestBody,
    HttpServletRequest request
)
```

- [ ] **2단계: 서비스 메서드 추가**

```java
public MissingRunningDistanceGetResponse getMissingRunningDistance(String privateId)
public EventRunningDistancePatchResponse patchRunningDistance(Long eventId, String privateId, BigDecimal distance)
```

규칙:
- 주최자만 거리 등록 가능
- 조회는 현재 로그인 사용자가 주최한 종료 이벤트 중 `expectedRunningDistanceKm == null`인 것만 반환
- 등록은 `expectedRunningDistanceKm`만 갱신

- [ ] **3단계: 테스트 실행**

```bash
cd run && ./gradlew test --tests "*EventRenewalServiceTest"
```

기대 결과: 통과.

---

## 작업 7. 신청서 생성/수정/조회/취소 수정

**파일:**
- 수정: `run/src/main/java/com/guide/run/event/controller/EventFormController.java`
- 수정: `run/src/main/java/com/guide/run/event/service/EventFormService.java`
- 테스트: `run/src/test/java/com/guide/run/event/service/EventRenewalServiceTest.java`

- [ ] **1단계: 생성/수정 요청 DTO 교체**

`CreateEventForm` 대신 `EventApplyRequest`를 받도록 변경한다.

- [ ] **2단계: 신청서 생성 로직 수정**

생성 규칙:
- 같은 이벤트/사용자의 `APPLIED` 신청서가 있으면 실패
- 기존 `CANCELED` 신청서가 있어도 재신청 정책은 이번 범위에서 정의하지 않으므로 실패
- `status = APPLIED` 저장
- `COMPETITION`이면 `birthDate`, `phoneNumber` 필수
- 신청서 저장 후 추가답변 저장

- [ ] **3단계: 신청서 수정 로직 수정**

수정 대상은 `APPLIED` 신청서만 허용한다.

```java
EventForm form = eventFormRepository.findByEventIdAndPrivateIdAndStatus(
    eventId,
    privateId,
    EventFormStatus.APPLIED
);
```

기본 신청 정보, 대회 신청 정보, 추가답변을 갱신한다.

- [ ] **4단계: 내 신청서 조회 API 추가**

```java
@GetMapping("/{eventId}/form")
public ResponseEntity<MyEventApplyGetResponse> getMyForm(
    @PathVariable("eventId") Long eventId,
    HttpServletRequest request
)
```

응답에는 이벤트 정보, 사용자 정보, 신청 정보, 대회 신청 정보, 추가답변을 포함한다.

- [ ] **5단계: 신청 취소 로직 변경**

기존 `eventFormRepository.delete(form)`을 제거하고 아래 방식으로 변경한다.

```java
form.cancel(LocalDateTime.now());
eventFormRepository.save(form);
```

출석, 매칭, 미매칭 정리 로직은 유지한다.

- [ ] **6단계: 테스트 실행**

```bash
cd run && ./gradlew test --tests "*EventRenewalServiceTest"
```

기대 결과: 통과.

---

## 작업 8. 신청자 명단/신청자 신청서 API 구현

**파일:**
- 수정: `run/src/main/java/com/guide/run/event/controller/EventFormController.java`
- 수정: `run/src/main/java/com/guide/run/event/controller/EventAttendanceController.java`
- 수정: `run/src/main/java/com/guide/run/event/service/EventFormService.java`
- 수정: `run/src/main/java/com/guide/run/event/service/EventAttendService.java`

- [ ] **1단계: `/forms` 경로 소유권 이동**

현재 `EventAttendanceController`의 `GET /api/event/{eventId}/forms`는 출석 목록을 반환한다. 이 메서드를 제거하고, 같은 경로를 `EventFormController`로 옮겨 신청자 명단 응답을 반환하게 한다.

- [ ] **2단계: 신청자 명단 응답 구현**

대상은 `APPLIED` 신청서만 사용한다.

응답 구성:

```java
summary.totalCount = 전체 APPLIED 신청서 수
summary.viCount = APPLIED VI 신청서 수
summary.guideCount = APPLIED GUIDE 신청서 수
groups[].runningGroup = EventForm.hopeTeam
groups[].totalCount = 그룹별 신청서 수
groups[].applicants[] = userId, name, type, isFirstParticipation
```

`isFirstParticipation`는 아래 기준으로 계산한다.

```java
(user.getTrainingCnt() + user.getCompetitionCnt()) == 0
```

- [ ] **3단계: 신청자 신청서 상세 API 추가**

```java
@GetMapping("/{eventId}/forms/{userId}")
public ResponseEntity<EventApplicantFormResponse> getApplicantForm(
    @PathVariable("eventId") Long eventId,
    @PathVariable("userId") String userId,
    HttpServletRequest request
)
```

권한:
- 이벤트 주최자 또는 관리자만 조회 가능
- 그 외 사용자는 `NotEventOrganizerException`

- [ ] **4단계: 테스트 실행**

```bash
cd run && ./gradlew test --tests "*EventRenewalServiceTest"
```

기대 결과: 통과.

---

## 작업 9. 이벤트 상세 비회원 접근 보안 처리

**파일:**
- 수정: `run/src/main/java/com/guide/run/global/jwt/JwtAuthenticationFilter.java`
- 수정: `run/src/main/java/com/guide/run/global/security/config/SecurityConfig.java`

- [ ] **1단계: JWT 필터 수정**

Authorization 헤더가 없으면 예외를 던지지 않고 다음 필터로 넘긴다. 토큰이 제공됐는데 유효하지 않은 경우는 계속 실패시킨다.

```java
String bearer = ((HttpServletRequest) request).getHeader(HttpHeaders.AUTHORIZATION);
if (bearer == null || !bearer.startsWith("Bearer ")) {
    chain.doFilter(request, response);
    return;
}
String token = bearer.substring("Bearer ".length());
```

- [ ] **2단계: 숫자 eventId 상세 조회만 permitAll 처리**

`SecurityConfig`에 추가:

```java
.requestMatchers(new RegexRequestMatcher("^/api/event/[0-9]+$", "GET")).permitAll()
```

import:

```java
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
```

`/api/event/all`, `/api/event/search`, `/api/event/missing-running-distance`는 이 matcher에 걸리지 않아야 한다.

- [ ] **3단계: 수동 확인**

토큰 없이 이벤트 생성 호출:

```bash
curl -i -X POST http://localhost:4000/api/event
```

기대 결과: 인증 실패.

토큰 없이 이벤트 상세 호출:

```bash
curl -i http://localhost:4000/api/event/1
```

기대 결과: 존재하는 이벤트라면 200, `viewer = null`.

---

## 작업 10. 전체 검증

**파일:**
- 전체 수정 Java 파일

- [ ] **1단계: 컴파일**

```bash
cd run && ./gradlew compileJava
```

기대 결과: 통과.

- [ ] **2단계: 타겟 테스트**

```bash
cd run && ./gradlew test --tests "*EventAdditionalInfoServiceTest" --tests "*EventRenewalServiceTest"
```

기대 결과: 통과.

- [ ] **3단계: 이벤트 관련 테스트**

```bash
cd run && ./gradlew test --tests "*Event*"
```

기대 결과: 통과.

- [ ] **4단계: 수동 토큰 테스트**

전제: 로컬 DB에 `accountId = runner01`, `password = p@ssword123`인 승인된 테스트 계정이 있어야 한다.

로그인 후 토큰 저장:

```bash
ACCESS_TOKEN=$(curl -s -X POST http://localhost:4000/api/login \
  -H "Content-Type: application/json" \
  -d '{"accountId":"runner01","password":"p@ssword123"}' \
  | sed -n 's/.*"accessToken":"\([^"]*\)".*/\1/p')
```

토큰으로 상세 조회:

```bash
curl -s http://localhost:4000/api/event/1 \
  -H "Authorization: Bearer ${ACCESS_TOKEN}"
```

기대 결과: `viewer`가 null이 아닌 상세 응답.

- [ ] **5단계: diff 점검**

```bash
git diff --check
git status --short
```

기대 결과: 공백 오류 없음. 의도한 파일만 변경됨.
