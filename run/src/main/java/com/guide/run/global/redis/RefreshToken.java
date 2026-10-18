package com.guide.run.global.redis;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
// timeToLive is in SECONDS: 30 days, matching JwtProvider.REFRESH_TOKEN_VALID_TIME
@RedisHash(value = "refreshToken", timeToLive = 60L * 60 * 24 * 30)
public class RefreshToken {
    @Id
    private String token;
    private String privateId;
}
