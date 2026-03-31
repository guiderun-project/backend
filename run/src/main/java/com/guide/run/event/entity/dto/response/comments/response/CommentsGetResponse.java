package com.guide.run.event.entity.dto.response.comments.response;

import com.guide.run.event.entity.dto.response.comments.GetComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "이벤트 댓글 목록 응답")
public class CommentsGetResponse {
    @Schema(description = "댓글 목록")
    private List<GetComment> comments;
}
