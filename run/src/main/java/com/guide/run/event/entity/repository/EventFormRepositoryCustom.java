package com.guide.run.event.entity.repository;

import com.guide.run.event.entity.dto.response.form.Form;
import com.guide.run.user.entity.type.UserType;

import java.util.List;

public interface EventFormRepositoryCustom {
    List<Form> findAllEventIdAndUserType(Long eventId, UserType userType);
    List<Form> findAllEventIdAndUserTypeAndHopeTeam(Long eventId, UserType userType,String hopeTeam);
}
