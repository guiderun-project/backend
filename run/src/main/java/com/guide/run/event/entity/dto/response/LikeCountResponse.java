package com.guide.run.event.entity.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
public class LikeCountResponse {
    private Long likes;
    @JsonProperty(value = "isLiked")
    private Boolean isLiked; //boolean 하면 liked 값이 생겨버림
}
