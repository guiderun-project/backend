package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.EventAdditionalOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventAdditionalOptionRepository extends JpaRepository<EventAdditionalOption, Long> {
    List<EventAdditionalOption> findAllByQuestionIdOrderByDisplayOrderAsc(Long questionId);
    List<EventAdditionalOption> findAllByQuestionIdInOrderByDisplayOrderAsc(List<Long> questionIds);
    void deleteAllByQuestionIdIn(List<Long> questionIds);
}
