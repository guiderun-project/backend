package com.guide.run.global.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash(value = "authNumber", timeToLive = 1000L * 60 * 30)
public class TmpToken {
    @Id
    private String token;
    private String privateId;
}
