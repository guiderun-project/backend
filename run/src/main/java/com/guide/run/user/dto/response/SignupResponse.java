package com.guide.run.user.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupResponse {
    private String userId;
    private String accessToken;
    private String userStatus;
}
