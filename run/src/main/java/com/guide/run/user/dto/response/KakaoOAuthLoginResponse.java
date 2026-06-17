package com.guide.run.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KakaoOAuthLoginResponse {
    private String status;

    // LOGIN_SUCCESS
    private String accessToken;
    private UserInfo user;

    // SIGNUP_REQUIRED
    private String signupToken;
    private String provider;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private Role role;
        private UserType disabilityType;
    }
}
