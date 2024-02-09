package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventFormRepository extends JpaRepository <EventForm,Long> {
    List<EventForm> findByPrivateIdAndEndTimeBetweenOrderByEndTime(String privateId, LocalDateTime startTime,LocalDateTime endTime);
    List<EventForm> findByPrivateIdAndEndTimeBetweenOrderByEndTimeDesc(String privateId, LocalDateTime startTime,LocalDateTime endTime);
    EventForm findByEventIdAndPrivateId(Long eventId,String privateId);
    Page<EventForm> findAllByPrivateIdOrderByEndTime(String privateId,Pageable pageable);
    Page<EventForm> findAllByPrivateIdAndEventTypeOrderByEndTime(String privateId, EventType eventType,Pageable pageable);
    Page<EventForm> findAllByPrivateIdAndRecruitStatusOrderByEndTime(String privateId, EventRecruitStatus recruitStatus,Pageable pageable);
    Page<EventForm> findAllByPrivateIdAndRecruitStatusAndEventTypeOrderByEndTime(String privateId, EventRecruitStatus recruitStatus,EventType eventType,Pageable pageable);

    List<EventForm> findAllByPrivateId(String privateId);

    List<EventForm> findAllByPrivateIdAndEventType(String privateId, EventType eventType);

    List<EventForm> findAllByPrivateIdAndRecruitStatus(String privateId, EventRecruitStatus eventRecruitStatus);

    List<EventForm> findAllByPrivateIdAndRecruitStatusAndEventType(String privateId, EventRecruitStatus eventRecruitStatus, EventType eventType);

}
