package com.guide.run.event.entity.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class EventCommentCreateRequest {
    private String content;

    //dto json 변환시 객체 하나만 있으면 파업하므로 아래 어노테이션 붙여줘야함
    @JsonCreator
    public EventCommentCreateRequest(String content) {
        this.content = content;
    }
}
