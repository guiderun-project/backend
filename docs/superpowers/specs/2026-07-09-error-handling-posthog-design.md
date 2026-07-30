# Error Handling and PostHog Logging Design

## Objective

Improve error handling across `guiderun-backend` and `guiderun-front-v2` without changing successful API contracts. Backend error responses will be expanded in a backward-compatible way, frontend v2 will consistently consume those responses, and operationally relevant errors will be logged to PostHog.

## Scope

This work applies to:

- `guiderun-backend` branch `feature/error-handling-posthog`
- `guiderun-front-v2` branch `feature/error-handling-posthog`

The existing backend error body fields `errorCode` and `message` remain stable. New error-only fields may be added because frontend v2 has not depended on the error body shape beyond those fields.

Successful response DTOs and endpoint paths are out of scope.

## Backend Error Response Contract

All handled API errors should return the following shape:

```json
{
  "errorCode": "2200",
  "message": "이벤트 탭을 예정 이벤트, 지난 이벤트, 나의 이벤트 중에서 선택해주세요.",
  "status": 400,
  "path": "/api/event/all",
  "timestamp": "2026-07-09T10:20:30+09:00"
}
```

Validation errors should include field-level details:

```json
{
  "errorCode": "7000",
  "message": "입력값이 올바르지 않아요.",
  "status": 400,
  "path": "/api/event",
  "timestamp": "2026-07-09T10:20:30+09:00",
  "fieldErrors": [
    {
      "field": "eventContent",
      "message": "모임 내용은 500자 이하로 입력해주세요."
    }
  ]
}
```

The backend should preserve existing advice classes where practical and add the minimum shared helpers needed to avoid duplicating `status`, `path`, and `timestamp` creation.

## Backend Exception Handling

Concrete exception handling will be added for currently under-specified cases:

- `ResponseStatusException`: return its own HTTP status and reason instead of falling through to unknown 500.
- `MethodArgumentNotValidException`: return `7000`, a general validation message, and `fieldErrors`.
- Request-value `IllegalArgumentException`: return 400 with a stable error code and safe message.
- Upload and image conversion failures: separate user-input image errors from server-side storage failures.
- TossPayments lookup failures: return a clear external integration failure, preferably 502 when the upstream call/response is invalid.
- Partner and image persistence failures: return a clear server-side processing failure instead of a generic unknown where possible.
- Authorization inconsistency: align `notExistAuthorization` with the authentication failure behavior used by JWT error responses.

`UnknownExceptionAdvice` remains the final fallback for unhandled errors.

## Backend Message Updates

The following messages will be updated to match frontend Korean UI labels and the existing `front-v2` friendly tone:

- `notValidSort`: `이벤트 탭을 예정 이벤트, 지난 이벤트, 나의 이벤트 중에서 선택해주세요.`
- `notValidType`: `이벤트 유형을 전체, 대회, 훈련 중에서 선택해주세요.`
- `notValidKind`: `모집구분을 전체, 모집중, 모집예정, 모집마감, 종료 중에서 선택해주세요.`
- `notValidYear`: `연도 선택 값이 올바르지 않아요.`
- `notValidMonth`: `월 선택 값이 올바르지 않아요.`
- `notValidDay`: `일자 선택 값이 올바르지 않아요.`

## Frontend Error Model

Frontend v2 will introduce a normalized API error model:

```ts
type ApiErrorKind =
  | 'validation'
  | 'auth'
  | 'permission'
  | 'notFound'
  | 'conflict'
  | 'server'
  | 'network'
  | 'unknown';

type ApiError = {
  message: string;
  status?: number;
  errorCode?: string;
  path?: string;
  fieldErrors?: Array<{ field: string; message: string }>;
  method?: string;
  url?: string;
  kind: ApiErrorKind;
};
```

`handleApiRequest` will convert Axios errors into this model. Consumers should call shared helpers rather than reading `error.response.data` directly.

## Frontend Error Presentation

Frontend v2 should use backend messages first and fall back to existing page-level copy when no safe message exists.

- Query screens: `QueryBoundary` displays the normalized error message when available and keeps retry support.
- Mutations: existing `window.alert('...실패했어요')` handlers should use the normalized backend message first. Toast usage can remain where already established.
- Authentication: 401 handling clears session and routes through existing login/session behavior.
- Permission errors: 403 shows a permission or approval-related message.
- Not-found errors: detail pages may show a not-found state, while nullable lookup flows may continue to return `null`.
- Validation errors: `fieldErrors` should be used by forms where wiring is straightforward; otherwise the root message is shown.

## PostHog Logging

Frontend v2 already has `@posthog/react` and `posthog-js`. This work will enable exception capture and add targeted manual logging.

- `PostHogProvider` options will enable exception capture for unhandled errors and unhandled promise rejections.
- `ErrorBoundary` will manually capture render errors with route and component stack.
- API errors with `server`, `network`, or `unknown` kind will be captured as exceptions.
- Routine 4xx business errors will not be captured as exceptions by default. They may be captured as a low-noise `api_error` event with status, errorCode, method, and URL.

No request body, token, phone number, name, or other sensitive user input should be sent to PostHog.

## Verification

Backend verification:

```bash
./gradlew test
```

Frontend v2 verification:

```bash
pnpm lint
pnpm build
```

Focused tests should cover:

- expanded backend error response fields,
- validation `fieldErrors`,
- `ResponseStatusException` handling,
- frontend API error normalization,
- QueryBoundary fallback behavior,
- PostHog logging filters for 4xx vs 5xx/network errors.
