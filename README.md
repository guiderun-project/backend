# ERD
![가이드러너 db](https://github.com/user-attachments/assets/54a93b5c-f1e1-4c72-b38d-5a67891ba2b1)

## 로컬 실행

### 1) 환경변수 로드 후 실행 (권장)
`application-local.yml`에서 DB URL 등을 환경변수(`TEST_RDS`, `MYSQL_USERNAME`, `MYSQL_PASSWORD` 등)로 읽습니다.
`.env`를 로드하지 않으면 `${TEST_RDS}`가 문자열 그대로 들어가 실행이 실패할 수 있습니다.

```bash
cd run
set -a
source .env
set +a
./gradlew bootRun
```

### 2) 확인 방법
아래 값이 비어있지 않으면 정상 로드된 상태입니다.

```bash
echo $TEST_RDS
```

### 3) IntelliJ로 실행할 때
Run Configuration의 Environment variables에 최소 아래 키를 넣어야 합니다.

- `TEST_RDS`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`
