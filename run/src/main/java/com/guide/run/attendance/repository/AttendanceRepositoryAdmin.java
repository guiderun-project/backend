package com.guide.run.attendance.repository;

import com.guide.run.admin.dto.response.event.AbsentDto;

public interface AttendanceRepositoryAdmin {
    AbsentDto getAbsentList(long eventId);
}
