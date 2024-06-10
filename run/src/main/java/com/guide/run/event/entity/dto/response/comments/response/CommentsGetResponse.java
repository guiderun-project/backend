package com.guide.run.event.entity.dto.response.comments.response;

import com.guide.run.event.entity.dto.response.comments.GetComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CommentsGetResponse {
    private List<GetComment> comments;
}
