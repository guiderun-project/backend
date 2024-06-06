package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.admin.dto.EventHistoryDto;
import com.guide.run.admin.dto.condition.EventSortCond;
import com.guide.run.admin.dto.response.event.CurrentEventResponse;
import com.guide.run.event.entity.dto.response.get.MyPageEvent;

import java.util.List;

public interface EventRepositoryAdmin {
    List<MyPageEvent> findMyEventAfterYear(String privateId, int start, int limit, String kind, int year);

    long countMyEventAfterYear(String privateId, String kind, int year);

    List<EventDto> getAdminEventList(int start, int limit, EventSortCond cond);

    long getAdminEventCount();

    List<CurrentEventResponse> findCurrentEvent(int start, int limit);

    List<EventHistoryDto> getUserEventHistory(String privateId, int start, int limit, String kind);

    long getUserEventHistoryCount(String privateId, String kind);

    List<EventDto> searchAdminEvent(String text, int start, int limit, EventSortCond cond);
    long searchAdminEventCount(String text);

    List<EventHistoryDto> searchUserEventHistory(String privateId, String text, int start, int limit);
    long searchUserEventHistoryCount(String privateId, String text);

}
