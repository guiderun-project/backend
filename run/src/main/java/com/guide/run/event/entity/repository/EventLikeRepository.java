package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventLikeRepository extends JpaRepository<EventLike,Long> {
    Optional<EventLike> findByEventIdAndPrivateId(Long eventId, String privateId);
    Long countByEventId(Long eventId);
    void deleteAllByPrivateId(String privateId);
    void deleteAllByEventId(Long eventId);
}
