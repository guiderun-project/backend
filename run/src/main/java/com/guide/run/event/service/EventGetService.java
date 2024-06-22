package com.guide.run.event.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.response.get.*;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.repository.EventRepositoryImpl;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                    return eventRepository.countByRecruitStatusNotAndIsApprove(RECRUIT_END,true);
                }
                else{
                    return eventRepository.countByRecruitStatusAndIsApprove(kind,true);
                }
            }else{
                if(kind.equals(RECRUIT_ALL)){
                    return eventRepository.countByTypeAndRecruitStatusNotAndIsApprove(type, RECRUIT_END,true);
                }
                else{
                    return eventRepository.countByTypeAndRecruitStatusAndIsApprove(type,kind,true);
                }
            }
        }else if(sort.equals("END")){
            if(type.equals(TOTAL)){
                return eventRepository.countByRecruitStatusAndIsApprove(RECRUIT_END,true);
            }else{
                return eventRepository.countByTypeAndRecruitStatusAndIsApprove(type, RECRUIT_END,true);
            }
        }else{
            if(type.equals(TOTAL)){
                if(kind.equals(RECRUIT_ALL)){
                    return eventFormRepository.countByPrivateIdAndIsApprove(privateId,true);
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

    public AllEventResponse getAllEventList(int limit,
                                            int start,
                                            String sort,
                                            EventType type,
                                            EventRecruitStatus kind,
                                            String userId) {
        List<AllEvent> allEvents = new ArrayList<>();
        if(sort.equals("UPCOMING")){
            if(kind.equals(RECRUIT_END))
                throw new NotValidKindException();
            if(type.equals(TOTAL)){
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.getAllEventList(limit,start,null,RECRUIT_ALL);
                }
                else{
                    allEvents = eventRepository.getAllEventList(limit,start,null,kind);
                }
            }else{
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.getAllEventList(limit,start,type,RECRUIT_ALL);
                }
                else{
                    allEvents = eventRepository.getAllEventList(limit,start,type,kind);
                }
            }
        }else if(sort.equals("END")){
            if(type.equals(TOTAL)){
                allEvents = eventRepository.getAllEventList(limit,start,null, RECRUIT_END);
            }else{
                allEvents = eventRepository.getAllEventList(limit,start,type,RECRUIT_END);
            }
        }else{
            if(type.equals(TOTAL)){
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.getAllMyEventList(limit,start,null,null,userId);
                }
                else{
                    allEvents = eventRepository.getAllMyEventList(limit,start,null,kind,userId);
                }
            }else{
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.getAllMyEventList(limit,start,type,null,userId);
                }
                else{
                    allEvents = eventRepository.getAllMyEventList(limit,start,type,kind,userId);
                }
            }
        }
        return AllEventResponse.builder().items(allEvents).build();
    }
}
