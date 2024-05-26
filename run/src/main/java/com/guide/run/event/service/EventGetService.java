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
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.guide.run.event.entity.type.EventRecruitStatus.*;
import static com.guide.run.event.entity.type.EventType.TOTAL;

@Service
@RequiredArgsConstructor
public class EventGetService {
    private final EventRepository eventRepository;
    private final EventFormRepository eventFormRepository;


    public MyEventResponse getMyEvent(String sort, int year,String privateId) {
       List<MyEvent> myEvents;
       if(sort.equals("UPCOMING")){
           myEvents = eventRepository.findMyEventByYear(privateId, year, RECRUIT_ALL);
       }else if(sort.equals("END")){
           myEvents = eventRepository.findMyEventByYear(privateId, year, RECRUIT_END);
       }else{
           throw new NotValidSortException();
       }
       return MyEventResponse.builder().items(myEvents).build();
    }

    public long getAllEventListCount(String sort, EventType type, EventRecruitStatus kind, String privateId){
        if(sort.equals("UPCOMING")){
            if(type.equals(TOTAL)){
                if(kind.equals(RECRUIT_ALL)){
                    return eventRepository.findAllByRecruitStatusNot(RECRUIT_END).size();
                }
                else{
                    return eventRepository.findAllByRecruitStatus(kind).size();
                }
            }else{
                if(kind.equals(RECRUIT_ALL)){
                    return eventRepository.findAllByTypeAndRecruitStatusNot(type, RECRUIT_END).size();
                }
                else{
                    return eventRepository.findAllByTypeAndRecruitStatus(type,kind).size();
                }
            }
        }else if(sort.equals("END")){
            if(type.equals(TOTAL)){
                return eventRepository.findAllByRecruitStatus(RECRUIT_END).size();
            }else{
                return eventRepository.findAllByTypeAndRecruitStatus(type, RECRUIT_END).size();
            }
        }else{
            if(type.equals(TOTAL)){
                if(kind.equals(RECRUIT_ALL)){
                    return eventFormRepository.findAllByPrivateId(privateId).size();
                }
                else{
                    return eventRepository.getAllMyEventListCount(null,kind,privateId);
                }
            }else{
                if(kind.equals(RECRUIT_ALL)){
                    return eventRepository.getAllMyEventListCount(type, null,privateId);
                }
                else{
                    return eventRepository.getAllMyEventListCount(type,kind,privateId);
                }
            }
        }
    }
}
