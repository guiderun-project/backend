package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventFormRepository extends JpaRepository <EventForm,Long> {
    List<EventForm> findByPrivateIdAndEndTimeBetweenOrderByEndTime(String privateId, LocalDateTime startTime,LocalDateTime endTime);
    List<EventForm> findByPrivateIdAndEndTimeBetweenOrderByEndTimeDesc(String privateId, LocalDateTime startTime,LocalDateTime endTime);
    EventForm findByEventIdAndPrivateId(Long eventId,String privateId);
}
