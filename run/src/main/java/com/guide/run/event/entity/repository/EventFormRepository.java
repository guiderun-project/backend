package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.type.EventFormStatus;
import com.guide.run.user.entity.type.UserType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface EventFormRepository extends JpaRepository <EventForm,Long> ,EventFormRepositoryAdmin,EventFormRepositoryCustom {
    EventForm findByEventIdAndPrivateId(Long eventId,String privateId);
    List<EventForm> findAllByEventIdAndPrivateId(Long eventId,String privateId);
    List<EventForm> findAllByPrivateId(String privateId);
    void deleteAllByEventId(Long eventId);
    void deleteAllByPrivateId(String privateId);
    List<EventForm> findAllByEventId(Long eventId);
    EventForm findByEventIdAndPrivateIdAndStatus(Long eventId, String privateId, EventFormStatus status);
    List<EventForm> findAllByEventIdAndStatus(Long eventId, EventFormStatus status);
    long countByEventIdAndStatus(Long eventId, EventFormStatus status);
    long countByEventIdAndTypeAndStatus(Long eventId, UserType type, EventFormStatus status);

    long countByPrivateId(String privateId);

    List<EventForm> findAllByPrivateIdAndEventIdIn(String privateId, List<Long> eventIds);

    @Query("""
            select event
            from EventForm form, Event event
            where form.eventId = event.id
              and form.privateId = :privateId
              and form.status = :status
              and event.endTime < :now
              and (form.runningDistanceKm is null or form.runningDistanceKm = :zero)
            order by event.endTime desc, event.id desc
            """)
    List<Event> findLatestMissingRunningDistanceEvents(
            @Param("privateId") String privateId,
            @Param("status") EventFormStatus status,
            @Param("now") LocalDateTime now,
            @Param("zero") BigDecimal zero,
            Pageable pageable
    );

    @Query("""
            select form
            from EventForm form
            where form.eventId = :eventId
              and form.status = :status
              and (form.runningDistanceKm is null or form.runningDistanceKm = :zero)
            """)
    List<EventForm> findAllMissingRunningDistanceForms(
            @Param("eventId") Long eventId,
            @Param("status") EventFormStatus status,
            @Param("zero") BigDecimal zero
    );
}
