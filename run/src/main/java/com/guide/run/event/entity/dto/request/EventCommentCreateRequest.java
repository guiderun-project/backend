package com.guide.run.event.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "이벤트 댓글 작성/수정 요청")
public class EventCommentCreateRequest {
    @Schema(description = "댓글 내용", example = "이번 주 토요일에도 참여할게요.")
    private String content;

    //dto json 변환시 객체 하나만 있으면 파업하므로 아래 어노테이션 붙여줘야함
    @JsonCreator
    public EventCommentCreateRequest(String content) {
        this.content = content;
    }
}
