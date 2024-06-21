package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventCommentRepository extends JpaRepository<Comment,Long> ,EventCommentRepositoryCustom{
    List<Comment> findAllByEventId(Long eventId);
    Long countByEventId(Long eventId);
    void deleteAllByEventId(long eventId);
    List<Comment> findAllByPrivateId(String privateId);
    void deleteAllByPrivateId(String privateId);
}
