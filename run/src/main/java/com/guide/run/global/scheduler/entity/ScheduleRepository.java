package com.guide.run.global.scheduler.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long>{
    Schedule findByEventId(Long eventId);
    void deleteAllByEventId(Long eventId);


}
