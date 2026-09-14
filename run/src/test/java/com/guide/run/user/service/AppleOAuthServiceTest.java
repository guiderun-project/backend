package com.guide.run.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppleOAuthServiceTest {
    private AppleOAuthService service;
    private ValueOperations<String, String> values;
    private JwtDecoder decoder;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        RedisTemplate<String, String> redis = mock(RedisTemplate.class);
        values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        decoder = mock(JwtDecoder.class);
        service = new AppleOAuthService(redis, mock(RestTemplate.class), decoder);
        ReflectionTestUtils.setField(service, "clientId", "com.guiderun.web.login");
        ReflectionTestUtils.setField(service, "redirectUri", "https://dev-api.guiderun.org/api/oauth/apple/callback");
    }

    private Jwt token(String audience, String nonce, Instant expiry) {
        return Jwt.withTokenValue("signed-token").header("alg", "RS256")
                .issuer("https://appleid.apple.com").subject("001.example")
                .audience(List.of(audience)).claim("nonce", nonce)
                .issuedAt(Instant.now().minusSeconds(10)).expiresAt(expiry).build();
    }

    @Test void validAppleSubjectBecomesProviderScopedIdentity() {
        when(decoder.decode("token")).thenReturn(token("com.guiderun.web.login", "nonce", Instant.now().plusSeconds(60)));
        assertEquals("apple001.example", service.verifiedPrivateId("token", "nonce"));
    }
    @Test void rejectsDifferentAudience() {
        when(decoder.decode("token")).thenReturn(token("another.app", "nonce", Instant.now().plusSeconds(60)));
        assertThrows(ResponseStatusException.class, () -> service.verifiedPrivateId("token", "nonce"));
    }
    @Test void rejectsDifferentNonce() {
        when(decoder.decode("token")).thenReturn(token("com.guiderun.web.login", "attacker", Instant.now().plusSeconds(60)));
        assertThrows(ResponseStatusException.class, () -> service.verifiedPrivateId("token", "nonce"));
    }
    @Test void rejectsExpiredToken() {
        when(decoder.decode("token")).thenReturn(token("com.guiderun.web.login", "nonce", Instant.now().minusSeconds(1)));
        assertThrows(ResponseStatusException.class, () -> service.verifiedPrivateId("token", "nonce"));
    }
    @Test void rejectsInvalidSignature() {
        when(decoder.decode("token")).thenThrow(new JwtException("invalid signature"));
        assertThrows(JwtException.class, () -> service.verifiedPrivateId("token", "nonce"));
    }
    @Test void ticketCanOnlyBeUsedOnce() {
        when(values.getAndDelete(anyString())).thenReturn("apple001.example:" + AppleOAuthService.challenge("browser-proof"), null);
        assertEquals("apple001.example", service.exchange("ticket", "browser-proof"));
        assertThrows(ResponseStatusException.class, () -> service.exchange("ticket", "browser-proof"));
    }
    @Test void rejectsTicketFromAnotherBrowser() {
        when(values.getAndDelete(anyString())).thenReturn("apple001.example:" + AppleOAuthService.challenge("other-browser"));
        assertThrows(ResponseStatusException.class, () -> service.exchange("ticket", "this-browser"));
    }
    @Test void rejectsMissingOrExpiredTicket() {
        assertThrows(ResponseStatusException.class, () -> service.exchange("missing", "proof"));
    }
    @Test void disabledConfigurationDoesNotStartAuthorization() {
        ReflectionTestUtils.setField(service, "frontendUrl", "https://guiderun.org");
        ReflectionTestUtils.setField(service, "privateKey", "");
        assertThrows(ResponseStatusException.class, () -> service.start("challenge"));
        verifyNoInteractions(values);
    }
}
