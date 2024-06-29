package com.guide.run.global.sms.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SmsResponse {
    String requestId;
    LocalDateTime requestTime;
    String statusCode;
    String statusName;
    String authNum;

    public void setAuthNum(String authNum){
        this.authNum = authNum;
    }
}
