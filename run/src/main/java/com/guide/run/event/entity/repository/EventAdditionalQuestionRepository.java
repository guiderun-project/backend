package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventAdditionalQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventAdditionalQuestionRepository extends JpaRepository<EventAdditionalQuestion, Long> {
    List<EventAdditionalQuestion> findAllByEventIdOrderByDisplayOrderAsc(Long eventId);
    void deleteAllByEventId(Long eventId);
}
