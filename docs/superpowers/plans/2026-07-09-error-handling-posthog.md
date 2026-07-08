# 에러 처리 및 PostHog 로깅 구현 계획

> **작업자 필수 지침:** 이 계획을 실행할 때는 `superpowers:subagent-driven-development` 또는 `superpowers:executing-plans`를 사용해서 작업 단위별로 체크박스를 갱신한다. 각 작업은 테스트를 먼저 추가하거나 기존 실패 지점을 고정한 뒤 구현한다.

**목표:** 백엔드는 하위 호환 에러 응답을 확장하고 예외를 구체화하며, `front-v2`는 해당 에러 응답을 실제 UI와 PostHog 로깅에 사용한다.

**구조:** 백엔드는 `FailResult`/`ValidFailResult`와 전역 exception advice 계층을 중심으로 확장한다. 프론트는 `ApiError` 정규화 유틸을 만들고 `QueryBoundary`, mutation error handler, `ErrorBoundary`, PostHog 설정에 연결한다.

**기술 스택:** Java 17, Spring Boot, Spring MVC, Gradle, React 19, TypeScript, Axios, TanStack Query, PostHog, pnpm.

---

## 변경 파일 구조

### Backend: `/Users/pride_sd/project/guiderun-project/guiderun-backend`

생성:
- `run/src/main/java/com/guide/run/global/dto/response/FieldErrorResult.java`
- `run/src/main/java/com/guide/run/global/exception/GlobalExceptionAdvice.java`
- `run/src/main/java/com/guide/run/global/exception/ErrorResponseFactory.java`
- `run/src/test/java/com/guide/run/global/exception/ErrorResponseContractTest.java`
- `run/src/test/java/com/guide/run/global/exception/GlobalExceptionAdviceTest.java`

수정:
- `run/src/main/java/com/guide/run/global/dto/response/FailResult.java`
- `run/src/main/java/com/guide/run/global/dto/response/ValidFailResult.java`
- `run/src/main/java/com/guide/run/global/exception/validation/ValidationExceptionAdvice.java`
- `run/src/main/java/com/guide/run/global/exception/UnknownExceptionAdvice.java`
- `run/src/main/java/com/guide/run/global/jwt/JwtExceptionFilter.java`
- `run/src/main/resources/i18n/exception_ko.yml`

### Frontend v2: `/Users/pride_sd/project/guiderun-project/guiderun-front-v2`

생성:
- `src/api/core/apiError.ts`
- `src/api/core/errorLogging.ts`

수정:
- `src/api/types/error.ts`
- `src/api/core/request.ts`
- `src/api/core/client.ts`
- `src/components/ErrorBoundary/ErrorBoundary.tsx`
- `src/components/QueryBoundary/QueryBoundary.tsx`
- `src/contexts/AuthProvider.tsx`
- `src/main.tsx`
- API mutation onError가 있는 이벤트/회원가입 화면 파일들

---

## 작업 1. BE 에러 응답 DTO 확장

**파일:**
- 생성: `run/src/main/java/com/guide/run/global/dto/response/FieldErrorResult.java`
- 수정: `run/src/main/java/com/guide/run/global/dto/response/FailResult.java`
- 수정: `run/src/main/java/com/guide/run/global/dto/response/ValidFailResult.java`
- 테스트: `run/src/test/java/com/guide/run/global/exception/ErrorResponseContractTest.java`

- [ ] **1단계: 실패하는 DTO 계약 테스트 추가**

`ErrorResponseContractTest`를 생성한다.

```java
package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.dto.response.FieldErrorResult;
import com.guide.run.global.dto.response.ValidFailResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseContractTest {

    @Test
    void failResult는_기존_필드와_확장_필드를_함께_가진다() {
        FailResult result = new FailResult(
                "2200",
                "이벤트 탭을 예정 이벤트, 지난 이벤트, 나의 이벤트 중에서 선택해주세요.",
                400,
                "/api/event/all",
                "2026-07-09T10:20:30+09:00"
        );

        assertThat(result.getErrorCode()).isEqualTo("2200");
        assertThat(result.getMessage()).contains("이벤트 탭");
        assertThat(result.getStatus()).isEqualTo(400);
        assertThat(result.getPath()).isEqualTo("/api/event/all");
        assertThat(result.getTimestamp()).isNotBlank();
    }

    @Test
    void validFailResult는_fieldErrors를_포함한다() {
        ValidFailResult result = ValidFailResult.builder()
                .errorCode("7000")
                .message("입력값이 올바르지 않아요.")
                .status(400)
                .path("/api/event")
                .timestamp("2026-07-09T10:20:30+09:00")
                .fieldErrors(List.of(new FieldErrorResult("eventContent", "모임 내용은 500자 이하로 입력해주세요.")))
                .build();

        assertThat(result.getFieldErrors())
                .extracting(FieldErrorResult::getField)
                .containsExactly("eventContent");
    }
}
```

- [ ] **2단계: 테스트 실패 확인**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend/run
./gradlew test --tests "*ErrorResponseContractTest"
```

기대 결과: `FieldErrorResult`가 없거나 생성자/필드가 없어 컴파일 실패.

- [ ] **3단계: `FieldErrorResult` 추가**

```java
package com.guide.run.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorResult {
    private String field;
    private String message;
}
```

- [ ] **4단계: `FailResult` 확장**

기존 생성자 호출을 깨지 않도록 2개 필드 생성자도 유지한다.

```java
@Getter
@Schema(description = "실패 응답")
public class FailResult {
    @Schema(description = "에러 코드", example = "2200")
    private String errorCode;

    @Schema(description = "에러 메시지", example = "요청 값이 올바르지 않아요.")
    private String message;

    @Schema(description = "HTTP 상태 코드", example = "400")
    private Integer status;

    @Schema(description = "요청 경로", example = "/api/event/all")
    private String path;

    @Schema(description = "에러 발생 시각", example = "2026-07-09T10:20:30+09:00")
    private String timestamp;

    public FailResult(String errorCode, String message) {
        this(errorCode, message, null, null, null);
    }

    public FailResult(String errorCode, String message, Integer status, String path, String timestamp) {
        this.errorCode = errorCode;
        this.message = message;
        this.status = status;
        this.path = path;
        this.timestamp = timestamp;
    }
}
```

- [ ] **5단계: `ValidFailResult` 확장**

```java
@Data
@Builder
public class ValidFailResult {
    private String errorCode;
    private String message;
    private Integer status;
    private String path;
    private String timestamp;
    private List<FieldErrorResult> fieldErrors;
}
```

기존 `toResult(MethodArgumentNotValidException e)`는 작업 2에서 factory 기반으로 교체한다.

- [ ] **6단계: DTO 테스트 통과 확인**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend/run
./gradlew test --tests "*ErrorResponseContractTest"
```

기대 결과: PASS.

- [ ] **7단계: BE 커밋**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend
git add run/src/main/java/com/guide/run/global/dto/response run/src/test/java/com/guide/run/global/exception/ErrorResponseContractTest.java
git commit -m "feat: expand backend error response contract"
```

---

## 작업 2. BE 에러 응답 factory와 validation handler 정리

**파일:**
- 생성: `run/src/main/java/com/guide/run/global/exception/ErrorResponseFactory.java`
- 수정: `run/src/main/java/com/guide/run/global/exception/validation/ValidationExceptionAdvice.java`
- 수정: `run/src/main/java/com/guide/run/global/dto/response/ValidFailResult.java`
- 테스트: `run/src/test/java/com/guide/run/global/exception/GlobalExceptionAdviceTest.java`

- [ ] **1단계: validation 응답 테스트 추가**

`MockMvc` 기반으로 `MethodArgumentNotValidException`이 실제 JSON에 `fieldErrors`를 포함하는지 검증한다. 기존 `EventTextLengthValidationTest`가 있으므로 같은 컨트롤러 테스트 스타일을 따른다.

필수 assertion:

```java
andExpect(status().isBadRequest())
andExpect(jsonPath("$.errorCode").value("7000"))
andExpect(jsonPath("$.message").value("입력값이 올바르지 않아요."))
andExpect(jsonPath("$.status").value(400))
andExpect(jsonPath("$.path").exists())
andExpect(jsonPath("$.timestamp").exists())
andExpect(jsonPath("$.fieldErrors").isArray())
andExpect(jsonPath("$.fieldErrors[0].field").exists())
andExpect(jsonPath("$.fieldErrors[0].message").exists());
```

- [ ] **2단계: 테스트 실패 확인**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend/run
./gradlew test --tests "*GlobalExceptionAdviceTest"
```

기대 결과: `status/path/timestamp/fieldErrors`가 없어 실패.

- [ ] **3단계: `ErrorResponseFactory` 추가**

```java
package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import com.guide.run.global.dto.response.FieldErrorResult;
import com.guide.run.global.dto.response.ValidFailResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
public class ErrorResponseFactory {
    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    public FailResult fail(String errorCode, String message, HttpStatus status, HttpServletRequest request) {
        return new FailResult(
                errorCode,
                message,
                status.value(),
                request.getRequestURI(),
                timestamp()
        );
    }

    public ValidFailResult validation(String errorCode, String message, List<FieldError> fieldErrors, HttpServletRequest request) {
        List<FieldErrorResult> results = fieldErrors.stream()
                .map(fieldError -> new FieldErrorResult(fieldError.getField(), resolveFieldMessage(fieldError)))
                .toList();

        return ValidFailResult.builder()
                .errorCode(errorCode)
                .message(message)
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .timestamp(timestamp())
                .fieldErrors(results)
                .build();
    }

    private String timestamp() {
        return OffsetDateTime.now(SEOUL_ZONE).toString();
    }

    private String resolveFieldMessage(FieldError fieldError) {
        String defaultMessage = fieldError.getDefaultMessage();
        if (defaultMessage == null || defaultMessage.isBlank()) {
            return fieldError.getField() + " 입력값이 올바르지 않아요.";
        }
        return defaultMessage;
    }
}
```

- [ ] **4단계: validation advice 수정**

```java
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ValidationExceptionAdvice {
    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ValidFailResult> handleValidationException(
            MethodArgumentNotValidException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(errorResponseFactory.validation(
                "7000",
                "입력값이 올바르지 않아요.",
                e.getBindingResult().getFieldErrors(),
                request
        ));
    }
}
```

- [ ] **5단계: 테스트 통과 확인**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend/run
./gradlew test --tests "*GlobalExceptionAdviceTest"
```

기대 결과: PASS.

- [ ] **6단계: BE 커밋**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend
git add run/src/main/java/com/guide/run/global/exception/ErrorResponseFactory.java run/src/main/java/com/guide/run/global/exception/validation/ValidationExceptionAdvice.java run/src/main/java/com/guide/run/global/dto/response/ValidFailResult.java run/src/test/java/com/guide/run/global/exception/GlobalExceptionAdviceTest.java
git commit -m "feat: return field validation errors"
```

---

## 작업 3. BE 구체 예외 handler 추가

**파일:**
- 생성: `run/src/main/java/com/guide/run/global/exception/GlobalExceptionAdvice.java`
- 수정: `run/src/main/java/com/guide/run/global/exception/UnknownExceptionAdvice.java`
- 테스트: `run/src/test/java/com/guide/run/global/exception/GlobalExceptionAdviceTest.java`

- [ ] **1단계: `ResponseStatusException` 테스트 추가**

테스트는 `ResponseStatusException(HttpStatus.BAD_REQUEST, "userId and role are required.")`가 아래 응답을 반환하는지 확인한다.

```java
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
assertThat(response.getBody().getErrorCode()).isEqualTo("4000");
assertThat(response.getBody().getMessage()).isEqualTo("userId and role are required.");
assertThat(response.getBody().getStatus()).isEqualTo(400);
```

- [ ] **2단계: `IllegalArgumentException` 테스트 추가**

```java
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
assertThat(response.getBody().getErrorCode()).isEqualTo("4001");
assertThat(response.getBody().getMessage()).isEqualTo("요청 값이 올바르지 않아요.");
```

- [ ] **3단계: `IllegalStateException` 테스트 추가**

이번 구현에서는 `IllegalStateException`을 서버 내부 상태 오류로 보고 500으로 응답한다. TossPayments 같은 외부 연동 실패를 502로 세분화하는 작업은 별도 예외 클래스를 만들 때 처리한다.

```java
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
assertThat(response.getBody().getErrorCode()).isEqualTo("0000");
assertThat(response.getBody().getMessage()).isEqualTo("일시적인 오류가 발생했어요. 잠시 후 다시 시도해주세요.");
```

- [ ] **4단계: 테스트 실패 확인**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend/run
./gradlew test --tests "*GlobalExceptionAdviceTest"
```

기대 결과: handler가 없어 실패.

- [ ] **5단계: `GlobalExceptionAdvice` 추가**

```java
package com.guide.run.global.exception;

import com.guide.run.global.dto.response.FailResult;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(0)
public class GlobalExceptionAdvice {
    private final ErrorResponseFactory errorResponseFactory;

    @ExceptionHandler(ResponseStatusException.class)
    protected ResponseEntity<FailResult> handleResponseStatusException(
            ResponseStatusException e,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        String message = e.getReason() == null || e.getReason().isBlank()
                ? "요청을 처리하지 못했어요."
                : e.getReason();

        return ResponseEntity.status(status).body(errorResponseFactory.fail(
                String.valueOf(status.value() * 10),
                message,
                status,
                request
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<FailResult> handleIllegalArgumentException(
            IllegalArgumentException e,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(errorResponseFactory.fail(
                "4001",
                "요청 값이 올바르지 않아요.",
                HttpStatus.BAD_REQUEST,
                request
        ));
    }
}
```

`IllegalStateException`은 `UnknownExceptionAdvice`에서 기존 fallback으로 처리하되 메시지를 해요체로 바꾼다.

- [ ] **6단계: `UnknownExceptionAdvice` 메시지 factory 적용**

`UnknownExceptionAdvice`의 응답 생성도 `ErrorResponseFactory`를 사용하도록 바꾼다.

```java
return ResponseEntity.status(500).body(errorResponseFactory.fail(
        getMessage("unknown.code"),
        getMessage("unknown.msg"),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request
));
```

handler 시그니처에는 `HttpServletRequest request`를 추가한다.

- [ ] **7단계: 테스트 통과 확인**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend/run
./gradlew test --tests "*GlobalExceptionAdviceTest" --tests "*ExceptionAdviceOrderTest"
```

기대 결과: PASS.

- [ ] **8단계: BE 커밋**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend
git add run/src/main/java/com/guide/run/global/exception run/src/test/java/com/guide/run/global/exception
git commit -m "feat: handle common backend exceptions"
```

---

## 작업 4. BE 메시지와 인증 에러 정리

**파일:**
- 수정: `run/src/main/resources/i18n/exception_ko.yml`
- 수정: `run/src/main/java/com/guide/run/global/jwt/JwtExceptionFilter.java`
- 수정: `run/src/main/java/com/guide/run/global/exception/auth/AuthAuthorizeExceptionAdvice.java`
- 테스트: `run/src/test/java/com/guide/run/global/exception/event/EventLogicExceptionAdviceTest.java`

- [ ] **1단계: 이벤트 필터 메시지 테스트 추가**

`EventLogicExceptionAdviceTest`에 아래 메시지를 검증하는 테스트를 추가한다.

```java
assertThat(response.getBody().getMessage())
        .isEqualTo("이벤트 탭을 예정 이벤트, 지난 이벤트, 나의 이벤트 중에서 선택해주세요.");
```

`notValidType`, `notValidKind`, `notValidYear`, `notValidMonth`, `notValidDay`도 같은 방식으로 검증한다.

- [ ] **2단계: 테스트 실패 확인**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend/run
./gradlew test --tests "*EventLogicExceptionAdviceTest"
```

기대 결과: 기존 `잘못된 sort값 입니다.` 문구 때문에 실패.

- [ ] **3단계: `exception_ko.yml` 메시지 수정**

```yaml
unknown:
  code: "0000"
  msg: "일시적인 오류가 발생했어요. 잠시 후 다시 시도해주세요."

notValidSort:
  code: "2200"
  msg: "이벤트 탭을 예정 이벤트, 지난 이벤트, 나의 이벤트 중에서 선택해주세요."
notValidType:
  code: "2201"
  msg: "이벤트 유형을 전체, 대회, 훈련 중에서 선택해주세요."
notValidKind:
  code: "2202"
  msg: "모집구분을 전체, 모집중, 모집예정, 모집마감, 종료 중에서 선택해주세요."
notValidYear:
  code: "2204"
  msg: "연도 선택 값이 올바르지 않아요."
notValidMonth:
  code: "2205"
  msg: "월 선택 값이 올바르지 않아요."
notValidDay:
  code: "2206"
  msg: "일자 선택 값이 올바르지 않아요."
```

- [ ] **4단계: 인증 에러 status 정리**

`notExistAuthorization`은 JWT 필터와 advice 모두 401로 맞춘다.

`AuthAuthorizeExceptionAdvice`:

```java
@ExceptionHandler(NotExistAuthorizationException.class)
protected ResponseEntity<FailResult> NotExistAuthorizationException(NotExistAuthorizationException e) {
    return ResponseEntity.status(401).body(responseService.getFailResult(
            getMessage("notExistAuthorization.code"),
            getMessage("notExistAuthorization.msg")));
}
```

`JwtExceptionFilter`에서 같은 code/message를 사용하는지 확인하고, 응답 status가 401인지 유지한다.

- [ ] **5단계: 테스트 통과 확인**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend/run
./gradlew test --tests "*EventLogicExceptionAdviceTest" --tests "*ExceptionAdviceOrderTest"
```

기대 결과: PASS.

- [ ] **6단계: BE 커밋**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend
git add run/src/main/resources/i18n/exception_ko.yml run/src/main/java/com/guide/run/global/jwt/JwtExceptionFilter.java run/src/main/java/com/guide/run/global/exception/auth/AuthAuthorizeExceptionAdvice.java run/src/test/java/com/guide/run/global/exception/event/EventLogicExceptionAdviceTest.java
git commit -m "fix: align backend error messages"
```

---

## 작업 5. FE `ApiError` 정규화 추가

**파일:**
- 생성: `src/api/core/apiError.ts`
- 수정: `src/api/types/error.ts`
- 수정: `src/api/core/request.ts`
- 테스트: `src/api/core/apiError.test.ts`

- [ ] **1단계: `ErrorType` 확장**

`/Users/pride_sd/project/guiderun-project/guiderun-front-v2/src/api/types/error.ts`:

```ts
export type FieldErrorType = {
  field: string;
  message: string;
};

export type ErrorType = {
  errorCode: string;
  message: string;
  status?: number;
  path?: string;
  timestamp?: string;
  fieldErrors?: FieldErrorType[];
};
```

- [ ] **2단계: `apiError.ts` 구현**

```ts
import { isAxiosError } from 'axios';

import type { ErrorType, FieldErrorType } from '@/api/types/error';

export type ApiErrorKind =
  | 'validation'
  | 'auth'
  | 'permission'
  | 'notFound'
  | 'conflict'
  | 'server'
  | 'network'
  | 'unknown';

export class ApiError extends Error {
  status?: number;
  errorCode?: string;
  path?: string;
  timestamp?: string;
  fieldErrors?: FieldErrorType[];
  method?: string;
  url?: string;
  kind: ApiErrorKind;
  cause?: unknown;

  constructor(params: {
    message: string;
    kind: ApiErrorKind;
    status?: number;
    errorCode?: string;
    path?: string;
    timestamp?: string;
    fieldErrors?: FieldErrorType[];
    method?: string;
    url?: string;
    cause?: unknown;
  }) {
    super(params.message);
    this.name = 'ApiError';
    this.kind = params.kind;
    this.status = params.status;
    this.errorCode = params.errorCode;
    this.path = params.path;
    this.timestamp = params.timestamp;
    this.fieldErrors = params.fieldErrors;
    this.method = params.method;
    this.url = params.url;
    this.cause = params.cause;
  }
}

export const isApiError = (error: unknown): error is ApiError =>
  error instanceof ApiError;

export const getApiErrorMessage = (
  error: unknown,
  fallbackMessage: string,
) => {
  if (isApiError(error)) {
    return error.message || fallbackMessage;
  }
  return fallbackMessage;
};

export const isUnauthorizedApiError = (error: unknown) =>
  isApiError(error) && error.status === 401;

export const normalizeApiError = (error: unknown): ApiError => {
  if (!isAxiosError<ErrorType>(error)) {
    return new ApiError({
      message: '예상치 못한 오류가 발생했어요.',
      kind: 'unknown',
      cause: error,
    });
  }

  if (!error.response) {
    return new ApiError({
      message: '네트워크 연결을 확인해주세요.',
      kind: 'network',
      method: error.config?.method?.toUpperCase(),
      url: error.config?.url,
      cause: error,
    });
  }

  const status = error.response.status;
  const data = error.response.data;
  const message = data?.message || getFallbackMessage(status);

  return new ApiError({
    message,
    kind: resolveApiErrorKind(status),
    status,
    errorCode: data?.errorCode,
    path: data?.path,
    timestamp: data?.timestamp,
    fieldErrors: data?.fieldErrors,
    method: error.config?.method?.toUpperCase(),
    url: error.config?.url,
    cause: error,
  });
};

const resolveApiErrorKind = (status: number): ApiErrorKind => {
  if (status === 400) return 'validation';
  if (status === 401) return 'auth';
  if (status === 403) return 'permission';
  if (status === 404) return 'notFound';
  if (status === 409) return 'conflict';
  if (status >= 500) return 'server';
  return 'unknown';
};

const getFallbackMessage = (status: number) => {
  if (status === 401) return '로그인이 필요해요.';
  if (status === 403) return '접근 권한이 없어요.';
  if (status === 404) return '요청한 정보를 찾지 못했어요.';
  if (status >= 500) return '일시적인 오류가 발생했어요. 잠시 후 다시 시도해주세요.';
  return '요청을 처리하지 못했어요.';
};
```

- [ ] **3단계: `handleApiRequest` 연결**

```ts
import { normalizeApiError } from '@/api/core/apiError';

export const handleApiRequest = async <T>(request: () => Promise<T>) => {
  try {
    return await request();
  } catch (error) {
    throw normalizeApiError(error);
  }
};
```

- [ ] **4단계: 타입 검증**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-front-v2
pnpm build
```

기대 결과: `ApiError` 타입 관련 컴파일 성공.

- [ ] **5단계: FE 커밋**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-front-v2
git add src/api/core/apiError.ts src/api/core/request.ts src/api/types/error.ts
git commit -m "feat: normalize api errors"
```

---

## 작업 6. FE 에러 표시 흐름 적용

**파일:**
- 수정: `src/components/QueryBoundary/QueryBoundary.tsx`
- 수정: `src/contexts/AuthProvider.tsx`
- 수정: 이벤트 mutation hook 파일들

- [ ] **1단계: `QueryBoundary`에서 normalized message 사용**

fallback에 `error`를 받도록 변경한다.

```tsx
import { getApiErrorMessage } from '@/api/core/apiError';

fallback={({ error, reset: retry }) => (
  <Message role="alert">
    {getApiErrorMessage(error, errorMessage)}
    <Button level="secondary" size="s" type="button" onClick={retry}>
      다시 시도
    </Button>
  </Message>
)}
```

- [ ] **2단계: `AuthProvider` 401 판별 교체**

```ts
import { isUnauthorizedApiError } from '@/api/core/apiError';

const isUnauthorizedError = (error: unknown) => {
  return isUnauthorizedApiError(error);
};
```

토큰 재발급 요청은 `handleApiRequest`를 통과하므로 AxiosError 직접 판별을 제거한다.

- [ ] **3단계: 이벤트 생성 실패 메시지 교체**

`src/pages/events/new/useEventCreateMutation.ts`:

```ts
import { getApiErrorMessage } from '@/api/core/apiError';

onError: (error) => {
  window.alert(getApiErrorMessage(error, '모임 만들기에 실패했어요.'));
},
```

- [ ] **4단계: 이벤트 수정/삭제 실패 메시지 교체**

`src/pages/events/[eventId]/edit/useEventEditMutations.ts`:

```ts
onError: (error) => {
  window.alert(getApiErrorMessage(error, '모임 수정에 실패했어요.'));
},
```

삭제 mutation:

```ts
onError: (error) => {
  window.alert(getApiErrorMessage(error, '모집 게시글 삭제에 실패했어요.'));
},
```

- [ ] **5단계: 신청/수정/취소 실패 메시지 교체**

`src/pages/events/[eventId]/apply/useEventApplyPage.ts`의 생성 실패 handler:

```ts
onError: (error) => {
  window.alert(getApiErrorMessage(error, '참여 신청에 실패했어요.'));
},
```

`src/pages/events/[eventId]/apply/useEventApplyPage.ts`의 수정 실패 handler:

```ts
onError: (error) => {
  window.alert(getApiErrorMessage(error, '신청서 수정에 실패했어요.'));
},
```

`src/pages/events/[eventId]/hooks/useEventDetailCtaActionProps.ts`의 신청 취소 실패 handler:

```ts
onError: (error) => {
  window.alert(getApiErrorMessage(error, '신청 취소에 실패했어요.'));
},
```

- [ ] **6단계: 관리 액션 toast 메시지 교체**

`src/pages/events/[eventId]/hooks/useEventManagementActions.ts`:

```ts
onError: (error) => {
  showToast({
    type: 'error',
    icon: 'alert-circle-filled',
    content: getApiErrorMessage(error, '모집 마감에 실패했어요.'),
  });
},
```

- [ ] **7단계: FE 타입 검증**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-front-v2
pnpm build
```

기대 결과: PASS.

- [ ] **8단계: FE 커밋**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-front-v2
git add src/components/QueryBoundary/QueryBoundary.tsx src/contexts/AuthProvider.tsx src/pages/events
git commit -m "feat: display backend error messages"
```

---

## 작업 7. FE PostHog 에러 로깅 추가

**파일:**
- 생성: `src/api/core/errorLogging.ts`
- 수정: `src/main.tsx`
- 수정: `src/components/ErrorBoundary/ErrorBoundary.tsx`
- 수정: `src/api/core/request.ts`

- [ ] **1단계: PostHog 옵션에 예외 자동 수집 추가**

`src/main.tsx`:

```ts
const postHogOptions = {
  api_host: import.meta.env.VITE_POSTHOG_HOST,
  defaults: '2026-05-30',
  capture_exceptions: {
    capture_unhandled_errors: true,
    capture_unhandled_rejections: true,
    capture_console_errors: false,
  },
} as const;
```

- [ ] **2단계: `errorLogging.ts` 추가**

```ts
import posthog from 'posthog-js';

import { isApiError } from '@/api/core/apiError';

export const captureApiError = (error: unknown) => {
  if (!isApiError(error)) {
    posthog.captureException(error);
    return;
  }

  if (!['server', 'network', 'unknown'].includes(error.kind)) {
    posthog.capture('api_error', {
      status: error.status,
      errorCode: error.errorCode,
      kind: error.kind,
      method: error.method,
      url: error.url,
      path: error.path,
    });
    return;
  }

  posthog.captureException(error, {
    status: error.status,
    errorCode: error.errorCode,
    kind: error.kind,
    method: error.method,
    url: error.url,
    path: error.path,
  });
};

export const captureRenderError = (
  error: Error,
  info: { componentStack?: string },
) => {
  posthog.captureException(error, {
    route: window.location.pathname,
    componentStack: info.componentStack,
  });
};
```

- [ ] **3단계: `handleApiRequest`에 API 에러 로깅 연결**

```ts
import { normalizeApiError } from '@/api/core/apiError';
import { captureApiError } from '@/api/core/errorLogging';

export const handleApiRequest = async <T>(request: () => Promise<T>) => {
  try {
    return await request();
  } catch (error) {
    const apiError = normalizeApiError(error);
    captureApiError(apiError);
    throw apiError;
  }
};
```

- [ ] **4단계: `ErrorBoundary`에 `componentDidCatch` 추가**

```tsx
import { captureRenderError } from '@/api/core/errorLogging';
```

```tsx
componentDidCatch(error: Error, info: React.ErrorInfo): void {
  captureRenderError(error, { componentStack: info.componentStack ?? undefined });
}
```

`React.ErrorInfo` 타입을 쓰기 위해 import를 조정한다.

- [ ] **5단계: FE 검증**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-front-v2
pnpm lint
pnpm build
```

기대 결과: PASS.

- [ ] **6단계: FE 커밋**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-front-v2
git add src/main.tsx src/api/core src/components/ErrorBoundary/ErrorBoundary.tsx
git commit -m "feat: log frontend errors to posthog"
```

---

## 작업 8. 전체 검증 및 정리

**파일:**
- Backend 전체 변경 파일
- Frontend v2 전체 변경 파일

- [ ] **1단계: BE 전체 테스트**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend/run
./gradlew test
```

기대 결과: BUILD SUCCESSFUL.

- [ ] **2단계: FE lint**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-front-v2
pnpm lint
```

기대 결과: lint 오류 없음.

- [ ] **3단계: FE build**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-front-v2
pnpm build
```

기대 결과: TypeScript build와 Vite build 성공.

- [ ] **4단계: BE diff 점검**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-backend
git diff --check
git status --short
```

기대 결과: 공백 오류 없음. 의도한 파일만 변경됨.

- [ ] **5단계: FE diff 점검**

```bash
cd /Users/pride_sd/project/guiderun-project/guiderun-front-v2
git diff --check
git status --short
```

기대 결과: 공백 오류 없음. 의도한 파일만 변경됨.

- [ ] **6단계: 최종 요약 작성**

최종 응답에는 아래 내용을 포함한다.

```text
- BE 에러 응답 확장: errorCode/message 유지, status/path/timestamp/fieldErrors 추가
- BE 예외 구체화: ResponseStatusException, validation, request value error 처리
- FE 에러 소비: ApiError 정규화, QueryBoundary/mutation/AuthProvider 반영
- PostHog: 자동 예외 수집, ErrorBoundary/API 에러 로깅 추가
- 검증: ./gradlew test, pnpm lint, pnpm build
```
