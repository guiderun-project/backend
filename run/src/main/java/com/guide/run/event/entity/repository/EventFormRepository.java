package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventFormRepository extends JpaRepository <EventForm,Long> ,EventFormRepositoryAdmin,EventFormRepositoryCustom {
    EventForm findByEventIdAndPrivateId(Long eventId,String privateId);
    List<EventForm> findAllByEventIdAndPrivateId(Long eventId,String privateId);
    List<EventForm> findAllByPrivateId(String privateId);
    void deleteAllByEventId(Long eventId);
    void deleteAllByPrivateId(String privateId);
    List<EventForm> findAllByEventId(Long eventId);

    long countByPrivateId(String privateId);
}
