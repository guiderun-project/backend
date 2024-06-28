package com.guide.run.temp.member.repository;

import com.guide.run.event.entity.dto.response.attend.ParticipationInfo;
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.user.entity.type.UserType;

import java.util.List;

public interface AttendanceCustomRepository {
    Long countUserType(Long eventId, UserType userType);
    List<ParticipationInfo> getParticipationInfo(Long eventId, boolean isAttend);
    List<Attendance> getAttendanceTrue(Long eventId, boolean isAttend);
}
