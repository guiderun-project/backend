package com.guide.run.attendance.repository;

import com.guide.run.event.entity.dto.response.attend.AttendanceParticipant;

import java.util.List;

public interface AttendanceCustomRepository {
    List<AttendanceParticipant> findAttendanceParticipants(Long eventId, boolean isAttend);
}
