package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.response.get.MyEventResponse;
import com.guide.run.event.entity.dto.response.get.MyEvent;
import com.guide.run.event.entity.dto.response.get.UpcomingEvent;
import com.guide.run.event.entity.dto.response.get.UpcomingEventResponse;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.repository.EventRepositoryImpl;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventGetService {
    private final EventRepository eventRepository;
    private final EventFormRepository eventFormRepository;


    public MyEventResponse getMyEvent(String sort, int year,String privateId) {
       List<MyEvent> myEvents;
       if(sort.equals("UPCOMING")){
           myEvents = eventRepository.findMyEventByYear(privateId, year, EventRecruitStatus.RECRUIT_ALL);
       }else if(sort.equals("END")){
           myEvents = eventRepository.findMyEventByYear(privateId, year, EventRecruitStatus.RECRUIT_END);
       }else{
           throw new NotValidSortException();
       }
       return MyEventResponse.builder().items(myEvents).build();
    }
/*
    public UpcomingEventResponse getUpcomingEvent(String sort, String privateId) {
        List<UpcomingEvent> events = new ArrayList<>();
        int cnt = 4;
        if (sort.equals("OPEN")) {
            List<Event> findEvents = eventRepository.findByRecruitStatusOrderByEndTime(EventRecruitStatus.RECRUIT_OPEN);
            upcomingResponseCreate(privateId, events, cnt, findEvents);
            return UpcomingEventResponse.builder()
                    .items(events)
                    .build();
        } else if (sort.equals("UPCOMING")) {
            List<Event> findEvents = eventRepository.findByRecruitStatusOrderByEndTime(EventRecruitStatus.RECRUIT_UPCOMING);
            upcomingResponseCreate(privateId, events, cnt, findEvents);
            return UpcomingEventResponse.builder()
                    .items(events)
                    .build();
        }
        throw new NotValidSortException();
    }

    private void upcomingResponseCreate(String privateId, List<UpcomingEvent> events, int cnt, List<Event> findEvents) {
        for (Event e : findEvents) {
            EventForm eventForm = eventFormRepository.findByEventIdAndPrivateId(e.getId(), privateId);
            if (eventForm != null) {
                events.add(UpcomingEvent.builder()
                        .eventId(e.getId())
                        .eventType(e.getType())
                        .name(e.getName())
                        .isApply(true)
                        .date(e.getEndTime().toLocalDate())
                        .recruitStatus(e.getRecruitStatus())
                        .build());
            } else {
                events.add(UpcomingEvent.builder()
                        .eventId(e.getId())
                        .eventType(e.getType())
                        .name(e.getName())
                        .isApply(false)
                        .date(e.getEndTime().toLocalDate())
                        .recruitStatus(e.getRecruitStatus())
                        .build());
            }
            cnt--;
            if (cnt <= 0) break;
        }
    }
*/
}
