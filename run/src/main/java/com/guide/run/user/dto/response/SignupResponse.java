package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "회원가입 완료 응답")
public class SignupResponse {
    @Schema(description = "생성된 공개 사용자 ID", example = "guide_102")
    private String userId;
    @Schema(description = "회원가입 완료 후 사용할 액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "회원 상태", example = "USER")
    private String userStatus;
}
