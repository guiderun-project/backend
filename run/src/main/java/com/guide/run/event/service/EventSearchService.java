package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.get.AllEvent;
import com.guide.run.event.entity.dto.response.search.SearchAllEvent;
import com.guide.run.event.entity.dto.response.search.SearchAllEventList;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.guide.run.event.entity.type.EventRecruitStatus.RECRUIT_ALL;
import static com.guide.run.event.entity.type.EventRecruitStatus.RECRUIT_END;
import static com.guide.run.event.entity.type.EventType.TOTAL;

@Service
@RequiredArgsConstructor
public class EventSearchService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    //관리자는 예정/종료 이벤트 검색에서 비공개 이벤트까지 조회할 수 있다.
    private boolean isAdmin(String privateId) {
        if (privateId == null) {
            return false;
        }
        return userRepository.findUserByPrivateId(privateId)
                .map(user -> user.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }

    public long getSearchAllEventsCountValue(String title, String sort, EventType type, EventRecruitStatus kind, String privateId, CityName cityName) {
        return getSearchAllEventsCountValue(title, sort, type, kind, privateId, cityName, isAdmin(privateId));
    }

    private long getSearchAllEventsCountValue(String title, String sort, EventType type, EventRecruitStatus kind, String privateId, CityName cityName, boolean includePrivate) {
        if (sort.equals("UPCOMING")) {
            if (type.equals(TOTAL)) {
                return eventRepository.upcomingGetSearchEventListCount(title, null, kind.equals(RECRUIT_ALL) ? RECRUIT_ALL : kind, cityName, includePrivate);
            } else {
                return eventRepository.upcomingGetSearchEventListCount(title, type, kind.equals(RECRUIT_ALL) ? RECRUIT_ALL : kind, cityName, includePrivate);
            }
        } else if (sort.equals("END")) {
            if (type.equals(TOTAL)) {
                return eventRepository.pastGetSearchEventListCount(title, null, cityName, includePrivate);
            } else {
                return eventRepository.pastGetSearchEventListCount(title, type, cityName, includePrivate);
            }
        } else {
            if (type.equals(TOTAL)) {
                if (kind.equals(RECRUIT_ALL)) {
                    return eventRepository.getMySearchEventListCount(title, null, null, privateId, cityName);
                } else {
                    return eventRepository.getMySearchEventListCount(title, null, kind, privateId, cityName);
                }
            } else {
                if (kind.equals(RECRUIT_ALL)) {
                    return eventRepository.getMySearchEventListCount(title, type, null, privateId, cityName);
                } else {
                    return eventRepository.getMySearchEventListCount(title, type, kind, privateId, cityName);
                }
            }
        }
    }

    public SearchAllEventList getSearchAllEvents(int start, int limit, int page, String title, String sort, EventType type, EventRecruitStatus kind, String privateId, CityName cityName) {
        List<AllEvent> allEvents = new ArrayList<>();
        boolean includePrivate = isAdmin(privateId);

        if (sort.equals("UPCOMING")) {
            if (kind.equals(RECRUIT_END)) throw new NotValidKindException();
            if (type.equals(TOTAL)) {
                allEvents = eventRepository.upcomingGetSearchEventList(limit, start, title, null, kind, cityName, includePrivate);
            } else {
                allEvents = eventRepository.upcomingGetSearchEventList(limit, start, title, type, kind, cityName, includePrivate);
            }
        } else if (sort.equals("END")) {
            if (type.equals(TOTAL)) {
                allEvents = eventRepository.pastGetSearchEventList(limit, start, title, null, cityName, includePrivate);
            } else {
                allEvents = eventRepository.pastGetSearchEventList(limit, start, title, type, cityName, includePrivate);
            }
        } else {
            if (type.equals(TOTAL)) {
                if (kind.equals(RECRUIT_ALL)) {
                    allEvents = eventRepository.getMySearchEventList(limit, start, title, null, null, privateId, cityName);
                } else {
                    allEvents = eventRepository.getMySearchEventList(limit, start, title, null, kind, privateId, cityName);
                }
            } else {
                if (kind.equals(RECRUIT_ALL)) {
                    allEvents = eventRepository.getMySearchEventList(limit, start, title, type, null, privateId, cityName);
                } else {
                    allEvents = eventRepository.getMySearchEventList(limit, start, title, type, kind, privateId, cityName);
                }
            }
        }

        long totalCount = getSearchAllEventsCountValue(title, sort, type, kind, privateId, cityName, includePrivate);

        List<SearchAllEvent> items = allEvents.stream()
                .map(ae -> SearchAllEvent.builder()
                        .id(ae.getId())
                        .recruitStatus(ae.getRecruitStatus())
                        .name(ae.getName())
                        .type(ae.getType())
                        .dateText(ae.getDateText())
                        .build())
                .collect(Collectors.toList());

        return SearchAllEventList.builder()
                .items(items)
                .pagination(SearchAllEventList.Pagination.of(page, limit, totalCount))
                .build();
    }
}
