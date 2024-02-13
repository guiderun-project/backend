package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.response.search.MyEventResponse;
import com.guide.run.event.entity.dto.response.search.MyEvent;
import com.guide.run.event.entity.dto.response.search.UpcomingEvent;
import com.guide.run.event.entity.dto.response.search.UpcomingEventResponse;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.global.exception.event.logic.NotValidSortException;
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

    public MyEventResponse getMyEvent(String sort, int year, int month, String privateId) {
        List<MyEvent> myEvents = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);
        int cnt = 4;
        LocalDateTime endTime = LocalDateTime.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth(), 23, 59);
        List<EventForm> myEventForms;
        if (sort.equals("OPEN")) {
            myEventForms = eventFormRepository.findByPrivateIdAndEndTimeBetweenOrderByEndTime(privateId, startTime, endTime);
            for (EventForm eventForm : myEventForms) {
                if (eventForm.isMatching()) {
                    Event findEvent = eventRepository.findByIdAndRecruitStatus(eventForm.getEventId(), EventRecruitStatus.OPEN);
                    if (findEvent != null) {
                        myEvents.add(MyEvent.builder()
                                .eventId(findEvent.getId())
                                .eventType(findEvent.getType())
                                .name(findEvent.getName())
                                .dDay(Math.toIntExact(ChronoUnit.DAYS.between(findEvent.getEndTime().toLocalDate(), LocalDate.now())))
                                .endDate(findEvent.getEndTime().toLocalDate())
                                .recruitStatus(findEvent.getRecruitStatus())
                                .build());
                        cnt--;
                    }
                }
                if (cnt <= 0) break;
            }
            return MyEventResponse.builder()
                    .items(myEvents)
                    .build();
        } else if (sort.equals("CLOSE")) {
            myEventForms = eventFormRepository.findByPrivateIdAndEndTimeBetweenOrderByEndTimeDesc(privateId, startTime, endTime);
            for (EventForm eventForm : myEventForms) {
                if (eventForm.isMatching()) {
                    Event findEvent = eventRepository.findByIdAndRecruitStatus(eventForm.getEventId(), EventRecruitStatus.CLOSE);
                    if (findEvent != null) {
                        myEvents.add(MyEvent.builder()
                                .eventId(findEvent.getId())
                                .eventType(findEvent.getType())
                                .name(findEvent.getName())
                                .dDay(Math.toIntExact(ChronoUnit.DAYS.between(findEvent.getEndTime().toLocalDate(), LocalDate.now())))
                                .endDate(findEvent.getEndTime().toLocalDate())
                                .recruitStatus(findEvent.getRecruitStatus())
                                .build());
                        cnt--;
                    }
                }
                if (cnt <= 0) break;
            }
            return MyEventResponse.builder()
                    .items(myEvents)
                    .build();
        }
        throw new NotValidSortException();
    }

    public UpcomingEventResponse getUpcomingEvent(String sort, String privateId) {
        List<UpcomingEvent> events = new ArrayList<>();
        int cnt = 4;
        if (sort.equals("OPEN")) {
            List<Event> findEvents = eventRepository.findByRecruitStatusOrderByEndTime(EventRecruitStatus.OPEN);
            upcomingResponseCreate(privateId, events, cnt, findEvents);
            return UpcomingEventResponse.builder()
                    .items(events)
                    .build();
        } else if (sort.equals("UPCOMING")) {
            List<Event> findEvents = eventRepository.findByRecruitStatusOrderByEndTime(EventRecruitStatus.UPCOMING);
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

}
