package com.guide.run.attendance.repository;

import com.guide.run.attendance.entity.Attendance;
import com.guide.run.attendance.entity.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> ,  AttendanceCustomRepository, AttendanceRepositoryAdmin{
    List<Attendance> findAllByPrivateId(String privateId);
    Attendance findByEventIdAndPrivateId(Long eventId,String privateId);
    Long countByIsAttendAndEventId(boolean isAttend,Long eventId);

    void deleteAllByEventId(long eventId);

    List<Attendance> findAllByEventIdAndIsAttend(long eventId, boolean attend);
    List<Attendance> findAllByEventId(Long eventId);

    List<Attendance> findAllByPrivateIdAndIsAttend(String privateId,boolean isAttend);
}
