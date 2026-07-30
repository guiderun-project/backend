package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventAdditionalAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventAdditionalAnswerRepository extends JpaRepository<EventAdditionalAnswer, Long> {
    List<EventAdditionalAnswer> findAllByEventFormId(Long eventFormId);
    void deleteAllByEventFormId(Long eventFormId);
    void deleteAllByQuestionIdIn(List<Long> questionIds);
}
