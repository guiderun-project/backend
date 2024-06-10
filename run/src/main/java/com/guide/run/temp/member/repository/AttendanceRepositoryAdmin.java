package com.guide.run.temp.member.repository;

import com.guide.run.admin.dto.response.event.AbsentDto;

public interface AttendanceRepositoryAdmin {
    AbsentDto getAbsentList(long eventId);
}
