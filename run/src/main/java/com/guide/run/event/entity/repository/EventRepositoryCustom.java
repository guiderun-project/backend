package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.event.entity.dto.response.search.MyPageEvent;

import java.util.List;

public interface EventRepositoryCustom {
    List<MyPageEvent> findMyEventAfterYear(String privateId, int start, int limit, String kind, int year);

    long countMyEventAfterYear(String privateId, String kind, int year);

    List<EventDto> sortAdminEvent(int start, int limit);
    long sortAdminEventCount();

}
