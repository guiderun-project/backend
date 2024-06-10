package com.guide.run.global.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "tmpToken", timeToLive = 1000L * 60 * 30)
public class TmpToken {
    @Id
    private String token;
    private String privateId;
    private String type;

    public TmpToken(String token, String privateId, String type) {
        this.token = token;
        this.privateId = privateId;
        this.type = type;
    }
}
