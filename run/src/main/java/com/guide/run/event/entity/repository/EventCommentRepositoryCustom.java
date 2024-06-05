package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.dto.response.comments.GetComment;

import java.util.List;

public interface EventCommentRepositoryCustom {
    List<GetComment> findGetComments(int limit,int start,Long eventId);
}
