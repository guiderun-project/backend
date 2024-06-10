package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventFormRepository extends JpaRepository <EventForm,Long> ,EventFormRepositoryAdmin {
    EventForm findByEventIdAndPrivateId(Long eventId,String privateId);
    List<EventForm> findAllByEventIdAndPrivateId(Long eventId,String privateId);
    List<EventForm> findAllByPrivateId(String privateId);

    void deleteAllByEventId(Long eventId);

}
