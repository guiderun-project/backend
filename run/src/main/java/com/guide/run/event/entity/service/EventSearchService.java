package com.guide.run.event.entity.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.MyEvent;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventSearchService {
    private final EventRepository eventRepository;
    private final EventFormRepository eventFormRepository;

    public List<MyEvent> getMyEvent(String sort,int year,int month,String privateId){
        List<MyEvent> myEvents = new ArrayList<>();
        LocalDateTime startTime= LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth(), 23, 59);
        if (sort.equals("OPEN")) {
            List<EventForm> myEventForms = eventFormRepository.findByPrivateIdAndEndTimeBetweenOrderByEndTime(privateId,startTime,endTime);
            for(EventForm eventForm: myEventForms){
                if(eventForm.isMatching()){
                    Event findEvent = eventRepository.findByIdAndRecruitStatus(eventForm.getEventId(),EventRecruitStatus.OPEN);
                    int dDay = Period.between(findEvent.getEndTime().toLocalDate(), LocalDate.now()).getDays();
                    if(dDay >=0) {
                        myEvents.add(MyEvent.builder()
                                .eventId(findEvent.getId())
                                .eventType(findEvent.getType())
                                .name(findEvent.getName())
                                .dDay(dDay)
                                .endDate(findEvent.getEndTime().toLocalDate())
                                .recruitStatus(EventRecruitStatus.OPEN)
                                .build());
                    }
                }
            }
            return myEvents;
        }
        else if(sort.equals("END")){

        }
            throw new NotValidSortException();
    }
}
