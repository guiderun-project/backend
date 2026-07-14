package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.CommentLike;
import com.guide.run.event.entity.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    void deleteAllByCommentId(long commentId);
    void deleteAllByPrivateId(String privateId);
}
