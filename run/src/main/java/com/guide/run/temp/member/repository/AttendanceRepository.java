package com.guide.run.temp.member.repository;

import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.entity.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {
    List<Attendance> findAllByPrivateId(String privateId);
}
