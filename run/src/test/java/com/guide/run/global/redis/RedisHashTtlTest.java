package com.guide.run.global.redis;

import com.guide.run.global.jwt.JwtProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisHash;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link RedisHash#timeToLive()} is in SECONDS. These tests pin the Redis TTLs to the
 * matching JWT validity periods (which are in milliseconds) so the two cannot drift apart.
 */
class RedisHashTtlTest {

    private static long ttlSeconds(Class<?> type) {
        RedisHash hash = type.getAnnotation(RedisHash.class);
        assertThat(hash).as("@RedisHash on %s", type.getSimpleName()).isNotNull();
        return hash.timeToLive();
    }

    @Test
    @DisplayName("refreshToken Redis TTL equals the refresh JWT validity (30 days, in seconds)")
    void refreshTokenTtlMatchesJwtValidity() {
        assertThat(ttlSeconds(RefreshToken.class))
                .isEqualTo(JwtProvider.REFRESH_TOKEN_VALID_TIME / 1000)
                .isEqualTo(60L * 60 * 24 * 30);
    }

    @Test
    @DisplayName("tmpToken Redis TTL equals the temporary JWT validity (30 minutes, in seconds)")
    void tmpTokenTtlMatchesJwtValidity() {
        assertThat(ttlSeconds(TmpToken.class))
                .isEqualTo(JwtProvider.TMP_VALID_TIME / 1000)
                .isEqualTo(60L * 30);
    }

    @Test
    @DisplayName("authNumber Redis TTL stays at 10 minutes")
    void authNumberTtlIsTenMinutes() {
        assertThat(ttlSeconds(AuthNumber.class)).isEqualTo(600L);
    }
}
