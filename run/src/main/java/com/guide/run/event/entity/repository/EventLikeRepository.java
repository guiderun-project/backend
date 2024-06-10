package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLikeRepository extends JpaRepository<EventLike,Long> {
}
