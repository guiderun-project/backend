package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.get.*;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.CityName;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.logic.NotValidKindException;
import com.guide.run.global.exception.event.logic.NotValidSortException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public long getAllEventListCount(String sort, EventType type, EventRecruitStatus kind, String privateId, CityName cityName) {
        if (sort.equals("UPCOMING")) {
            if (type.equals(TOTAL)) {
                if (kind.equals(RECRUIT_ALL)) {
                    if (cityName == null) {
                        return eventRepository.countByRecruitStatusNotAndIsApprove(RECRUIT_END, true);
                    }
                    return eventRepository.countByRecruitStatusNotAndIsApproveAndCityName(RECRUIT_END, true, cityName);
                } else {
                    if (cityName == null) {
                        return eventRepository.countByRecruitStatusAndIsApprove(kind, true);
                    }
                    return eventRepository.countByRecruitStatusAndIsApproveAndCityName(kind, true, cityName);
                }
            } else {
                if (kind.equals(RECRUIT_ALL)) {
                    if (cityName == null) {
                        return eventRepository.countByTypeAndRecruitStatusNotAndIsApprove(type, RECRUIT_END, true);
                    }
                    return eventRepository.countByTypeAndRecruitStatusNotAndIsApproveAndCityName(type, RECRUIT_END, true, cityName);
                } else {
                    if (cityName == null) {
                        return eventRepository.countByTypeAndRecruitStatusAndIsApprove(type, kind, true);
                    }
                    return eventRepository.countByTypeAndRecruitStatusAndIsApproveAndCityName(type, kind, true, cityName);
                }
            }
        } else if (sort.equals("END")) {
            if (type.equals(TOTAL)) {
                if (cityName == null) {
                    return eventRepository.countByRecruitStatusAndIsApprove(RECRUIT_END, true);
                }
                return eventRepository.countByRecruitStatusAndIsApproveAndCityName(RECRUIT_END, true, cityName);
            } else {
                if (cityName == null) {
                    return eventRepository.countByTypeAndRecruitStatusAndIsApprove(type, RECRUIT_END, true);
                }
                return eventRepository.countByTypeAndRecruitStatusAndIsApproveAndCityName(type, RECRUIT_END, true, cityName);
            }
        } else {
            if (type.equals(TOTAL)) {
                if (kind.equals(RECRUIT_ALL)) {
                    if (cityName == null) {
                        return eventFormRepository.countByPrivateId(privateId);
                    }
                    return eventRepository.countByPrivateIdAndCityName(privateId, cityName);
                } else {
                    return eventRepository.getAllMyEventListCount(null, kind, privateId, cityName);
                }
            } else {
                if (kind.equals(RECRUIT_ALL)) {
                    return eventRepository.getAllMyEventListCount(type, null, privateId, cityName);
                } else {
                    return eventRepository.getAllMyEventListCount(type, kind, privateId, cityName);
                }
            }
        }
    }

    public AllEventResponse getAllEventList(int limit,
                                            int start,
                                            String sort,
                                            EventType type,
                                            EventRecruitStatus kind,
                                            String userId,
                                            CityName cityName) {
        List<AllEvent> allEvents = new ArrayList<>();
        if(sort.equals("UPCOMING")){
            if(kind.equals(RECRUIT_END))
                throw new NotValidKindException();
            if(type.equals(TOTAL)){
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.upcomingGetAllEventList(limit,start,null,RECRUIT_ALL,cityName);
                }
                else{
                    allEvents = eventRepository.upcomingGetAllEventList(limit,start,null,kind,cityName);
                }
            }else{
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.upcomingGetAllEventList(limit,start,type,RECRUIT_ALL,cityName);
                }
                else{
                    allEvents = eventRepository.upcomingGetAllEventList(limit,start,type,kind,cityName);
                }
            }
        }else if(sort.equals("END")){
            if(type.equals(TOTAL)){
                allEvents = eventRepository.getAllEventList(limit,start,null, RECRUIT_END,cityName);
            }else{
                allEvents = eventRepository.getAllEventList(limit,start,type,RECRUIT_END,cityName);
            }
        }else{
            if(type.equals(TOTAL)){
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.getAllMyEventList(limit,start,null,null,userId,cityName);
                }
                else{
                    allEvents = eventRepository.getAllMyEventList(limit,start,null,kind,userId,cityName);
                }
            }else{
                if(kind.equals(RECRUIT_ALL)){
                    allEvents = eventRepository.getAllMyEventList(limit,start,type,null,userId,cityName);
                }
                else{
                    allEvents = eventRepository.getAllMyEventList(limit,start,type,kind,userId,cityName);
                }
            }
        }
        return AllEventResponse.builder().items(allEvents).build();
    }
}
