package com.guide.run.admin.service;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.admin.dto.EventHistoryDto;
import com.guide.run.admin.dto.EventTypeCountDto;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;

import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventService {
    private final UserRepository userRepository;
    private final EventFormRepository eventFormRepository;
    private final EventRepository eventRepository;
    public List<EventHistoryDto> getEventHistory(String userId, int start, int limit, int year, int month){
        Pageable pageable = PageRequest.of(start/limit,limit);
        String privateId = userRepository.findUserByUserId(userId).orElseThrow(() -> new NotExistUserException()).getPrivateId();
        List<EventHistoryDto> eventHistories = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth(), 23, 59);
        Page<EventForm> eventForms = eventFormRepository.findByPrivateIdAndEndTimeBetweenOrderByEndTime(privateId, startTime, endTime,pageable);
        for(EventForm ef : eventForms){
            Event event = eventRepository.findById(ef.getEventId()).orElseThrow(() -> new NotExistEventException());
            eventHistories.add(EventHistoryDto.builder()
                    .eventId(event.getId())
                    .eventType(event.getType())
                    .name(event.getName())
                    .endDate(event.getEndTime().toLocalDate())
                    .recruitStatus(event.getRecruitStatus())
                    .build());
        }
        return eventHistories;
    }
    public int getEventHistoryCount(String userId,int year,int month){
        String privateId = userRepository.findUserByUserId(userId).orElseThrow(() -> new NotExistUserException()).getPrivateId();
        List<EventHistoryDto> eventHistories = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth(), 23, 59);
        List<EventForm> eventForms = eventFormRepository.findByPrivateIdAndEndTimeBetweenOrderByEndTimeDesc(privateId, startTime, endTime);
        return eventForms.size();
    }

    public EventTypeCountDto getEventTypeCount(String userId) {
        String privateId = userRepository.findUserByUserId(userId).orElseThrow(() -> new NotExistUserException()).getPrivateId();
        return EventTypeCountDto.builder()
                .Competition(eventFormRepository.findAllByPrivateIdAndEventType(privateId, EventType.COMPETITION).size())
                .Training(eventFormRepository.findAllByPrivateIdAndEventType(privateId, EventType.TRAINING).size())
                .build();
    }


    public int getAllEventCount() {
        return eventRepository.findAll().size();
    }

    public List<EventDto> getAllEventList(int start, int limit){
        Pageable pageable = PageRequest.of(start/limit,limit,Sort.by("endTime").descending());
        List<EventDto> eventDtos = new ArrayList<>();
        Page<Event> events = eventRepository.findAll(pageable);
        for(Event e : events){
            User user = userRepository.findUserByPrivateId(e.getOrganizer()).orElseThrow(() -> new NotExistUserException());
            eventDtos.add(EventDto.builder()
                            .eventId(e.getId())
                            .title(e.getName())
                            .smallDate("["+e.getEndTime().getMonthValue()+"/"+e.getEndTime().getDayOfMonth()+"]")
                            .date(e.getEndTime().getYear()+"."+e.getEndTime().getMonthValue()+"."+e.getEndTime().getDayOfMonth()+
                                    calculatePM(e)+":"+e.getEndTime().getMinute()+":"+e.getEndTime().getSecond())
                            .organizer(user.getName())
                            .pace(user.getRecordDegree())
                            .recruitStatus(e.getRecruitStatus())
                            .approval(e.isApprove())
                            .participation(e.getMaxNumG()+e.getMaxNumV())
                            .guideParticipation(e.getMaxNumG())
                            .viParticipation(e.getMaxNumV())
                            .update_date(e.getEndTime().getYear()+"."+e.getEndTime().getMonthValue()+"."+e.getEndTime().getDayOfMonth())
                            .update_time(e.getEndTime().getHour()+":"+e.getEndTime().getMinute()+":"+e.getEndTime().getSecond())
                    .build());
        }
        return eventDtos;
    }
    public String calculatePM(Event e){
        if(e.getEndTime().getHour()>=12){
            return " PM "+(e.getEndTime().getHour()-12);
        }else{
            return " AM "+e.getEndTime().getHour();
        }
    }
}
