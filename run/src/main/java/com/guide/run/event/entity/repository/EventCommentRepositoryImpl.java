package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.dto.response.comments.GetComment;
import com.guide.run.user.entity.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.event.entity.QComment.comment1;
import static com.guide.run.event.entity.QCommentLike.commentLike;
import static com.guide.run.user.entity.user.QUser.user;

public class EventCommentRepositoryImpl implements  EventCommentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public EventCommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<GetComment> findGetComments(int limit, int start, Long eventId) {
        return queryFactory.select(Projections.constructor(GetComment.class,
                        comment1.commentId.as("commentId"),
                        user.name.as("name"),
                user.type.as("type"),
                comment1.comment.as("content"),
                comment1.createdAt.as("createdAt"),
                        commentLike.privateIds.as("likes")))
                .from(comment1)
                .join(user).on(user.privateId.eq(comment1.privateId))
                .join(commentLike).on(commentLike.commentId.eq(comment1.commentId))
                .orderBy(comment1.createdAt.asc())
                .offset(start)
                .limit(limit)
                .fetch();
    }
}
