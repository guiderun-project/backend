package com.guide.run.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AccountIdPhoneRequest {
    private String accountId;
    private String phoneNum;
}
