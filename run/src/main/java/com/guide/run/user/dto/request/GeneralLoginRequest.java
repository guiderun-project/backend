package com.guide.run.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralLoginRequest {
    private String accountId;
    private String password;
}
