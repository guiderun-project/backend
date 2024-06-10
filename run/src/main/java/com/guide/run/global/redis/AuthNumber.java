package com.guide.run.global.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "authNumber", timeToLive = 1000L * 60 * 10)
public class AuthNumber {
    @Id
    private String phone;
    @Indexed
    private String authNum;
    private String type;

    public AuthNumber(String phone, String authNum, String type) {
        this.authNum = authNum;
        this.phone = phone;
        this.type = type;
    }
}
