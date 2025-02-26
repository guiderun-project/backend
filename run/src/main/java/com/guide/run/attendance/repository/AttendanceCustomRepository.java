package com.guide.run.attendance.repository;

import com.guide.run.event.entity.dto.response.attend.ParticipationInfo;
import com.guide.run.attendance.entity.Attendance;
import com.guide.run.user.entity.type.UserType;

import java.util.List;

public interface AttendanceCustomRepository {
    Long countUserType(Long eventId, UserType userType);
    List<ParticipationInfo> getParticipationInfo(Long eventId, boolean isAttend);
    List<Attendance> getAttendanceTrue(Long eventId, boolean isAttend);
}
