package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.get.MyEventDday;
import com.guide.run.event.entity.dto.response.get.MyEventDdayResponse;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long>, EventRepositoryCustom, EventRepositoryAdmin {
    Page<Event> findAll( Pageable pageable);
    List<Event> findAllByNameContainingOrContentContaining(String name,String content);
    Page<Event> findAllByNameContainingOrContentContaining(String name,String content,Pageable pageable);

    List<Event> findAllByRecruitStatusNot(EventRecruitStatus eventRecruitStatus);

    List<Event> findAllByTypeAndRecruitStatusNot(EventType type, EventRecruitStatus eventRecruitStatus);

    List<Event> findAllByRecruitStatus(EventRecruitStatus eventRecruitStatus);

    List<Event> findAllByTypeAndRecruitStatus(EventType type, EventRecruitStatus eventRecruitStatus);

    long countByRecruitStatusNotAndIsApprove(EventRecruitStatus eventRecruitStatus, boolean isApprove);

    long countByRecruitStatusAndIsApprove(EventRecruitStatus kind, boolean isApprove);

    long countByTypeAndRecruitStatusNotAndIsApprove(EventType type, EventRecruitStatus eventRecruitStatus, boolean isApprove);

    long countByTypeAndRecruitStatusAndIsApprove(EventType type, EventRecruitStatus kind, boolean isApprove);
}
