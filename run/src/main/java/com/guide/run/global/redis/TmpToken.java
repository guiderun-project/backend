package com.guide.run.global.redis;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
// timeToLive is in SECONDS: 30 minutes, matching JwtProvider.TMP_VALID_TIME
@RedisHash(value = "tmpToken", timeToLive = 60L * 30)
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
