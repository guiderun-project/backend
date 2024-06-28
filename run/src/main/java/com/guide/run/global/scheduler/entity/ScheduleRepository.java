package com.guide.run.global.scheduler.entity;

import com.guide.run.event.entity.type.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>{
    Schedule findByEventId(Long eventId);
    void deleteAllByEventId(Long eventId);


}
