package com.guide.run.event.entity.dto.response.comments.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentsCreatedResponse {
    private Long commentId;
}
