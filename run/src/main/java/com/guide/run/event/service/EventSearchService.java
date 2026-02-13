package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.search.SearchAllEventsCount;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.search.SearchAllEvent;
import com.guide.run.event.entity.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EventSearchService {
    private final EventRepository eventRepository;
    public SearchAllEventsCount getSearchAllEventsCount(String title){
        return SearchAllEventsCount.builder()
                        .count(eventRepository.findAllByNameContainingOrContentContaining(title,title).size())
                .build();
    }
    public List<SearchAllEvent> getSearchAllEvents(int start, int limit, String title){
        Pageable pageable = PageRequest.of(start/limit,limit);
        Page<Event> findEventPage = eventRepository.findAllByNameContainingOrContentContaining(title, title, pageable);
        List<SearchAllEvent> findEventList = new ArrayList<>();
        for(Event e : findEventPage){
            findEventList.add(SearchAllEvent.builder()
                    .eventId(e.getId())
                    .eventType(e.getType())
                    .name(e.getName())
                    .endDate(translateEndDate(e.getEndTime()))
                    .recruitStatus(e.getRecruitStatus())
                    .build());
        }
        return findEventList;
    }
    public String translateEndDate(LocalDateTime time){
        StringBuilder sb = new StringBuilder();
        sb.append(time.getYear());
        sb.append(".");
        sb.append(time.getMonthValue());
        sb.append(".");
        sb.append(time.getDayOfMonth());
        sb.append(" ");
        sb.append(time.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN));
        return sb.toString();
    }
}
