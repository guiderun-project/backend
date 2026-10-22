package com.guide.run.global.redis;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "authNumber", timeToLive = 600L)
public class AuthNumber {
    @Id
    private String phone;
    @Indexed
    private String authNum;
    @Indexed
    private String verificationId;
    private String type;
    private boolean canExtend;
    private int attemptCount;

    public AuthNumber(String phone, String authNum, String type, String verificationId) {
        this.phone = phone;
        this.authNum = authNum;
        this.type = type;
        this.verificationId = verificationId;
        this.canExtend = true;
        this.attemptCount = 0;
    }

    public AuthNumber withCanExtendFalse() {
        AuthNumber updated = new AuthNumber(this.phone, this.authNum, this.type, this.verificationId);
        updated.canExtend = false;
        updated.attemptCount = this.attemptCount;
        return updated;
    }

    public AuthNumber withIncrementedAttempt() {
        AuthNumber updated = new AuthNumber(this.phone, this.authNum, this.type, this.verificationId);
        updated.canExtend = this.canExtend;
        updated.attemptCount = this.attemptCount + 1;
        return updated;
    }
}
