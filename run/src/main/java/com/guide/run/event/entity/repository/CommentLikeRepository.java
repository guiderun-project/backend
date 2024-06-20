package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    List<CommentLike> findAllByCommentId(Long commentId);
    Optional<CommentLike> findByCommentIdAndPrivateId(Long commentId, String privateId);
    Long countByCommentId(Long commentId);
}
