package com.guide.run.user.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/** Web authorization only. Apple identities remain separate until explicit account linking exists. */
@Service
public class AppleOAuthService {
    private static final String ISSUER = "https://appleid.apple.com";
    private final RedisTemplate<String, String> redis;
    private final RestTemplate http;
    private final JwtDecoder decoder;
    private final SecureRandom random = new SecureRandom();

    @Value("${apple.client-id:com.guiderun.web.login}") private String clientId;
    @Value("${apple.team-id:GBA565XJC8}") private String teamId;
    @Value("${apple.key-id:SXG46WWVLC}") private String keyId;
    @Value("${apple.private-key:}") private String privateKey;
    @Value("${apple.redirect-uri:}") private String redirectUri;
    @Value("${apple.frontend-url:}") private String frontendUrl;

    @Autowired
    public AppleOAuthService(@Qualifier("redisTemplate") RedisTemplate<String, String> redis) {
        this.redis = redis;
        this.http = new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(10)).build();
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(ISSUER + "/auth/keys")
                .restOperations(http).build();
        jwtDecoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(ISSUER));
        this.decoder = jwtDecoder;
    }

    // Separate constructor allows signature/claim validation tests without Apple network access.
    AppleOAuthService(RedisTemplate<String, String> redis, RestTemplate http, JwtDecoder decoder) {
        this.redis = redis;
        this.http = http;
        this.decoder = decoder;
    }

    public String start(String challenge) {
        requireConfiguration();
        String state = randomToken();
        String nonce = randomToken();
        redis.opsForValue().set(stateKey(state), nonce + ":" + challenge, Duration.ofMinutes(5));
        return UriComponentsBuilder.fromHttpUrl(ISSUER + "/auth/authorize")
                .queryParam("client_id", clientId).queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code").queryParam("response_mode", "form_post")
                .queryParam("state", state).queryParam("nonce", nonce)
                .build().encode().toUriString();
    }

    public String callback(String code, String state, String error) {
        requireConfiguration();
        String failure = frontendUrl + "/oauth?provider=apple&error=apple_login_failed";
        if (state == null || !state.matches("[A-Za-z0-9_-]{43}")) return failure;
        String transaction = redis.opsForValue().getAndDelete(stateKey(state));
        if (transaction == null) return failure;
        if ("access_denied".equals(error)) return frontendUrl + "/oauth?provider=apple&error=access_denied";
        if (error != null || code == null || code.isBlank()) return failure;
        try {
            String[] parts = transaction.split(":", 2);
            var form = new LinkedMultiValueMap<String, String>();
            form.add("client_id", clientId);
            form.add("client_secret", clientSecret());
            form.add("code", code);
            form.add("grant_type", "authorization_code");
            form.add("redirect_uri", redirectUri);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            Map<?, ?> tokens = http.postForObject(ISSUER + "/auth/token", new HttpEntity<>(form, headers), Map.class);
            if (tokens == null || !(tokens.get("id_token") instanceof String)) return failure;
            String privateId = verifiedPrivateId((String) tokens.get("id_token"), parts[0]);
            String ticket = randomToken();
            redis.opsForValue().set(ticketKey(ticket), privateId + ":" + parts[1], Duration.ofSeconds(60));
            // Fragment keeps the one-use ticket out of frontend access logs and Referer headers.
            return frontendUrl + "/oauth?provider=apple#ticket=" + ticket;
        } catch (Exception ignored) {
            // Never log Apple responses, authorization codes or signing credentials.
            return failure;
        }
    }

    String verifiedPrivateId(String idToken, String nonce) {
        Jwt jwt = decoder.decode(idToken); // RSA signature, issuer and temporal validation.
        if (!jwt.getAudience().contains(clientId) || !nonce.equals(jwt.getClaimAsString("nonce"))
                || jwt.getExpiresAt() == null || !jwt.getExpiresAt().isAfter(Instant.now())
                || jwt.getSubject() == null || jwt.getSubject().isBlank()
                || jwt.getSubject().contains(":")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Apple 인증에 실패했습니다.");
        }
        return "apple" + jwt.getSubject();
    }

    public String exchange(String ticket, String verifier) {
        String value = redis.opsForValue().getAndDelete(ticketKey(ticket));
        if (value != null) {
            String[] parts = value.split(":", 2);
            if (parts.length == 2 && MessageDigest.isEqual(parts[1].getBytes(StandardCharsets.US_ASCII),
                    challenge(verifier).getBytes(StandardCharsets.US_ASCII))) return parts[0];
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 만료되었습니다. 다시 시도해 주세요.");
    }

    private String clientSecret() throws Exception {
        ECPrivateKey key = signingKey();
        Instant now = Instant.now();
        SignedJWT jwt = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(keyId).build(),
                new JWTClaimsSet.Builder().issuer(teamId).subject(clientId).audience(ISSUER)
                        .issueTime(Date.from(now)).expirationTime(Date.from(now.plusSeconds(300))).build());
        jwt.sign(new ECDSASigner(key));
        return jwt.serialize();
    }

    private ECPrivateKey signingKey() throws Exception {
        String pem = privateKey.replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        return (ECPrivateKey) KeyFactory.getInstance("EC")
                .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(pem)));
    }

    private void requireConfiguration() {
        // Explicit environment pairing prevents a dev callback from issuing a production session.
        boolean production = "https://api.guiderun.org/api/oauth/apple/callback".equals(redirectUri)
                && "https://guiderun.org".equals(frontendUrl);
        boolean development = "https://dev-api.guiderun.org/api/oauth/apple/callback".equals(redirectUri)
                && "https://dev.guiderun.org".equals(frontendUrl);
        if ((!production && !development) || privateKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Apple 로그인이 아직 설정되지 않았습니다.");
        }
        try {
            signingKey();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Apple 로그인 키 설정이 올바르지 않습니다.");
        }
    }

    static String challenge(String verifier) {
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII)));
        } catch (java.security.NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
    }

    private String randomToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private String stateKey(String value) { return "apple:" + clientId + ":" + redirectUri + ":state:" + value; }
    private String ticketKey(String value) { return "apple:" + clientId + ":" + redirectUri + ":ticket:" + value; }
}
