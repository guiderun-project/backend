package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.dto.response.comments.GetComment;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.guide.run.event.entity.QComment.*;
import static com.guide.run.event.entity.QCommentLike.*;
import static com.guide.run.user.entity.user.QUser.*;

public class EventCommentRepositoryImpl implements  EventCommentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public EventCommentRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }
    @Override
    public List<GetComment> findGetComments(int limit, int start, Long eventId,String userId) {
        return queryFactory.select(Projections.constructor(GetComment.class,
                comment1.commentId.as("commentId"),
                user.name.as("name"),
                user.userId.as("userId"),
                user.type.as("type"),
                comment1.comment.as("content"),
                comment1.createdAt.as("createdAt"),
                        ExpressionUtils.as(JPAExpressions.select(commentLike.count())
                                .from(commentLike)
                                .where(commentLike.commentId.eq(comment1.commentId)),"likes")
                ))
                .from(comment1)
                .join(user).on(user.privateId.eq(comment1.privateId))
                .where(comment1.eventId.eq(eventId))
                .orderBy(comment1.createdAt.desc())
                .offset(start)
                .limit(limit)
                .fetch();
    }
}
