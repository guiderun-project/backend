package com.guide.run.global.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@AllArgsConstructor
@RedisHash(value = "authNumber", timeToLive = 1000L * 60 * 10)
public class AuthNumber {
    @Id
    private String phone;
    private String authNum;
}
