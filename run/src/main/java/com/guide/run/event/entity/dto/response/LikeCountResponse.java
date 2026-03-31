package com.guide.run.event.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "좋아요 수와 내 좋아요 여부 응답")
public class LikeCountResponse {
    @Schema(description = "총 좋아요 수", example = "14")
    private Long likes;
    @JsonProperty(value = "isLiked")
    @Schema(description = "현재 로그인 사용자의 좋아요 여부", example = "true")
    private Boolean isLiked; //boolean 하면 liked 값이 생겨버림
}
