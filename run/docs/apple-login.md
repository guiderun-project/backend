# Apple web login

Apple Developer Services ID: `com.guiderun.web.login`, Team ID: `GBA565XJC8`, Key ID: `SXG46WWVLC`.
Primary App ID: `com.guiderun.app`.

Set these server environment variables (Spring relaxed binding):

```dotenv
APPLE_CLIENT_ID=com.guiderun.web.login
APPLE_TEAM_ID=GBA565XJC8
APPLE_KEY_ID=SXG46WWVLC
APPLE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\\n...\\n-----END PRIVATE KEY-----"
# Development
APPLE_REDIRECT_URI=https://dev-api.guiderun.org/api/oauth/apple/callback
APPLE_FRONTEND_URL=https://dev.guiderun.org
# Production: replace both values together
# APPLE_REDIRECT_URI=https://api.guiderun.org/api/oauth/apple/callback
# APPLE_FRONTEND_URL=https://guiderun.org
```

`APPLE_PRIVATE_KEY`에는 `.p8` 파일의 전체 PEM 내용을 넣는다. 배포 환경이 실제 줄바꿈을 지원하지 않으면 위처럼 `\\n`으로 저장해도 된다. Git이나 프론트 환경변수에는 절대 넣지 않는다. 누락된 설정에서는 기존 로그인은 그대로 동작하고 Apple 시작 API만 503을 반환한다. Redis는 GETDEL을 지원해야 한다(Redis 6.2 이상). 각 환경은 별도 트랜잭션 네임스페이스를 사용한다.

## Contract

- POST `/api/oauth/apple/start`, JSON `{ "challenge": "base64url SHA-256 of browser verifier" }` -> `{ "authorizationUrl": "https://appleid.apple.com/..." }`. Verifier is 32 random bytes in base64url (43 chars), kept in the initiating tab's sessionStorage. State/nonce expire in 5 minutes.
- Apple POSTs form-urlencoded `code`, `state`, or `error` to `/api/oauth/apple/callback`. Configure proxy to forward this POST unchanged. Callback CORS permits Apple origin only. Authorization code is exchanged server-side; issuer/signature/expiry/audience/nonce are verified.
- Callback 303 redirects to configured FE `/oauth?provider=apple#ticket=...`. The ticket lives for 60 seconds and is single use. Errors use `?provider=apple&error=access_denied` or `apple_login_failed`.
- POST `/api/oauth/apple/exchange`, JSON `{ "ticket": "...", "verifier": "..." }` -> existing LOGIN_SUCCESS or SIGNUP_REQUIRED response, with provider APPLE for signup. Exchange verifies the browser binding and consumes the ticket atomically. Access token stays in FE memory, refresh token in HttpOnly cookie.

No email/name scopes are requested; signup already collects required profile data. Apple `sub` is stored as `apple{sub}` using the existing privateId convention. Existing Kakao accounts are NOT automatically linked. Account linking and Apple account revocation/withdrawal integration are follow-up work before native App Store release.

## Verification

Run `./gradlew test --tests '*AppleOAuthServiceTest'`. Real integration requires the key, reachable HTTPS callbacks, and Redis. Check new signup, returning user, user cancellation, invalid/replayed state, expired/replayed ticket and wrong browser verifier on dev before production.
