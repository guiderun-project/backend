# Swagger Contract Gaps

Swagger 문서는 실제 백엔드 계약을 기준으로 작성한다. 아래 항목은 프론트 helper 또는 mock과 현재 서버 구현 사이에 확인된 차이이며, 이번 작업에서는 문서화만 정리하고 런타임 계약은 변경하지 않았다.

## 확인된 괴리

### `GET /api/user/event-history/count/{userId}`

- 백엔드 컨트롤러는 query parameter 이름으로 `kind`를 받는다.
- 현재 프론트 helper `infoApi.eventHistoryCountGet`는 `sort`를 보내고 있다.
- Swagger에는 실제 서버 계약인 `kind` 기준으로 반영한다.

### `GET /api/user/mypage`

- 프론트 request helper와 mock handler에는 존재하지만, 현재 백엔드 컨트롤러에서는 동일 경로를 확인하지 못했다.
- 실제 프론트 화면의 사용처는 확인되지 않았으므로 이번 Swagger 보강 범위에서는 제외한다.
