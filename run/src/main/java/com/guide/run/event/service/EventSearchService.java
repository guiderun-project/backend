package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.get.AllEvent;
import com.guide.run.event.entity.dto.response.search.SearchAllEvent;
import com.guide.run.event.entity.dto.response.search.SearchAllEventList;
import com.guide.run.event.entity.dto.response.search.SearchAllEventsCount;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.logic.NotValidKindException;
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

    public long getSearchAllEventsCountValue(String title, String sort, EventType type, EventRecruitStatus kind, String privateId, CityName cityName) {
        if (sort.equals("UPCOMING")) {
            if (type.equals(TOTAL)) {
                return eventRepository.getSearchEventListCount(title, null, kind.equals(RECRUIT_ALL) ? RECRUIT_ALL : kind, cityName);
            } else {
                return eventRepository.getSearchEventListCount(title, type, kind.equals(RECRUIT_ALL) ? RECRUIT_ALL : kind, cityName);
            }
        } else if (sort.equals("END")) {
            if (type.equals(TOTAL)) {
                return eventRepository.getSearchEventListCount(title, null, RECRUIT_END, cityName);
            } else {
                return eventRepository.getSearchEventListCount(title, type, RECRUIT_END, cityName);
            }
        } else { // MY
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

    public SearchAllEventsCount getSearchAllEventsCount(String title, String sort, EventType type, Even