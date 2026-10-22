package com.guide.run.global.sms;

import com.guide.run.global.exception.user.dto.SmsRateLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsRateLimiter {
    private final StringRedisTemplate redisTemplate;

    // 공개 /api/sms/** 발송 남용(문자 폭탄·SMS 펌핑) 방지용 고정 윈도 카운터.
    private static final long PHONE_PER_MINUTE = 1;   // 같은 번호: 60초에 1회
    private static final long PHONE_PER_DAY = 5;      // 같은 번호: 하루 5회
    private static final long GLOBAL_PER_HOUR = 100;  // 전역: 시간당 100회(펌핑 급증 차단)

    public void requireSendAllowed(String phone) {
        hit("sms:rl:ph:" + phone + ":m", Duration.ofMinutes(1), PHONE_PER_MINUTE);
        hit("sms:rl:ph:" + phone + ":d", Duration.ofDays(1), PHONE_PER_DAY);
        hit("sms:rl:global:h", Duration.ofHours(1), GLOBAL_PER_HOUR);
    }

    private void hit(String key, Duration ttl, long max) {
        long count;
        try {
            Long incremented = redisTemplate.opsForValue().increment(key);
            count = (incremented == null) ? 1L : incremented;
            if (count == 1L) {
                redisTemplate.expire(key, ttl);
            }
        } catch (RuntimeException e) {
            // Redis 장애 시 발송 차단(fail-closed): 인증번호 저장도 Redis라 어차피 흐름이 중단되며,
            // 열어두면 SMS 비용 남용 위험이 크므로 막는 쪽을 택한다.
            log.warn("SMS rate limiter unavailable, blocking send. key={}, err={}", key, e.toString());
            throw new SmsRateLimitExceededException();
        }
        if (count > max) {
            throw new SmsRateLimitExceededException();
        }
    }
}
