package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    Event findByIdAndRecruitStatus(Long id,EventRecruitStatus recruitStatus);

    List<Event> findByRecruitStatusOrderByEndTime(EventRecruitStatus recruitStatus);
    Page<Event> findAllByTypeAndRecruitStatusOrderByEndTimeDesc(EventType eventType,
                                                                 EventRecruitStatus eventRecruitStatus,
                                                                 Pageable pageable);
    Page<Event> findAllByTypeAndRecruitStatusOrderByEndTime(EventType eventType,
                                                                EventRecruitStatus eventRecruitStatus,
                                                                Pageable pageable);
    Page<Event> findAllByTypeAndRecruitStatusNotOrderByEndTime(EventType eventType,
                                                               EventRecruitStatus eventRecruitStatus,
                                                               Pageable pageable);
    Page<Event> findAllByRecruitStatusNotOrderByEndTime(EventRecruitStatus eventRecruitStatus,
                                                               Pageable pageable);
    Page<Event> findAllByRecruitStatusOrderByEndTime(EventRecruitStatus eventRecruitStatus,
                                                     Pageable pageable);
    Page<Event> findAllByRecruitStatusOrderByEndTimeDesc(EventRecruitStatus eventRecruitStatus,Pageable pageable);

    List<Event> findAllByRecruitStatusNot(EventRecruitStatus eventRecruitStatus);

    List<Event> findAllByTypeAndRecruitStatusNot(EventType type, EventRecruitStatus eventRecruitStatus);

    List<Event> findAllByRecruitStatus(EventRecruitStatus eventRecruitStatus);

    List<Event> findAllByTypeAndRecruitStatus(EventType type, EventRecruitStatus eventRecruitStatus);
}
