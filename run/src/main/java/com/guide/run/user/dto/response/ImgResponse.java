package com.guide.run.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "프로필 이미지 업로드 응답")
public class ImgResponse {
    @Schema(description = "업로드된 이미지 URL", example = "https://cdn.guiderun.org/profile/guide_102.png")
    private String img;
}
