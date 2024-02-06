package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.type.EventRecruitStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    Event findByIdAndRecruitStatus(Long id,EventRecruitStatus recruitStatus);
    List<Event> findByRecruitStatusOrderByEndTime(EventRecruitStatus recruitStatus);
}
