package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.type.EventFormStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventFormRepository extends JpaRepository<EventForm, Long> {
    EventForm findByEventIdAndPrivateId(Long eventId,String privateId);
    List<EventForm> findAllByEventIdAndPrivateId(Long eventId,String privateId);
    List<EventForm> findAllByPrivateId(String privateId);
    void deleteAllByEventId(Long eventId);
    void deleteAllByPrivateId(String privateId);
    List<EventForm> findAllByEventId(Long eventId);
    EventForm findByEventIdAndPrivateIdAndStatus(Long eventId, String privateId, EventFormStatus status);
    List<EventForm> findAllByEventIdAndStatus(Long eventId, EventFormStatus status);
    long countByEventIdAndStatus(Long eventId, EventFormStatus status);
    long countByPrivateId(String privateId);
}
