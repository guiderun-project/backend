package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long>, EventRepositoryCustom {
    Page<Event> findAll( Pageable pageable);
    List<Event> findAllByOrganizer(String organizer);

    List<Event> findAllByOrganizerAndEndTimeBeforeOrderByEndTimeDescIdDesc(
            String organizer,
            LocalDateTime now,
            Pageable pageable
    );

}
