package com.guide.run.global.sms;

import com.guide.run.global.exception.user.dto.SmsRateLimitExceededException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmsRateLimiterTest {

    private static final String PHONE = "01012345678";
    private static final String MINUTE_KEY = "sms:rl:ph:01012345678:m";
    private static final String DAY_KEY = "sms:rl:ph:01012345678:d";
    private static final String GLOBAL_KEY = "sms:rl:global:h";

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;

    @InjectMocks private SmsRateLimiter rateLimiter;

    private void stubIncrement(String key, long value) {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(valueOps.increment(key)).thenReturn(value);
    }

    @Test
    @DisplayName("한도 이내 첫 발송은 허용하고, 새 카운터에 TTL을 설정한다")
    void allowsFirstSendAndSetsTtl() {
        stubIncrement(MINUTE_KEY, 1L);
        stubIncrement(DAY_KEY, 1L);
        stubIncrement(GLOBAL_KEY, 1L);

        assertThatCode(() -> rateLimiter.requireSendAllowed(PHONE)).doesNotThrowAnyException();

        verify(redisTemplate).expire(eq(MINUTE_KEY), any(Duration.class));
        verify(redisTemplate).expire(eq(DAY_KEY), any(Duration.class));
        verify(redisTemplate).expire(eq(GLOBAL_KEY), any(Duration.class));
    }

    @Test
    @DisplayName("같은 번호로 60초 내 재발송은 거부한다")
    void blocksSecondSendWithinMinute() {
        stubIncrement(MINUTE_KEY, 2L);

        assertThatThrownBy(() -> rateLimiter.requireSendAllowed(PHONE))
                .isInstanceOf(SmsRateLimitExceededException.class);
    }

    @Test
    @DisplayName("같은 번호의 하루 한도를 넘기면 거부한다")
    void blocksOverDailyLimit() {
        stubIncrement(MINUTE_KEY, 1L);
        stubIncrement(DAY_KEY, 6L);

        assertThatThrownBy(() -> rateLimiter.requireSendAllowed(PHONE))
                .isInstanceOf(SmsRateLimitExceededException.class);
    }

    @Test
    @DisplayName("전역 시간당 한도를 넘기면 거부한다 (문자 펌핑 방지)")
    void blocksOverGlobalHourlyLimit() {
        stubIncrement(MINUTE_KEY, 1L);
        stubIncrement(DAY_KEY, 1L);
        stubIncrement(GLOBAL_KEY, 101L);

        assertThatThrownBy(() -> rateLimiter.requireSendAllowed(PHONE))
                .isInstanceOf(SmsRateLimitExceededException.class);
    }

    @Test
    @DisplayName("Redis 장애 시 발송을 차단한다 (fail-closed)")
    void failsClosedOnRedisError() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOps);
        lenient().when(valueOps.increment(MINUTE_KEY)).thenThrow(new RuntimeException("redis down"));

        assertThatThrownBy(() -> rateLimiter.requireSendAllowed(PHONE))
                .isInstanceOf(SmsRateLimitExceededException.class);
    }
}
