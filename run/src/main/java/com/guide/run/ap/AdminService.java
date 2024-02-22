package com.guide.run.ap;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final EventFormRepository eventFormRepository;
    private final EventRepository eventRepository;
    public List<EventHistory> getEventHistory(String userId,int start,int limit,int year,int month){
        Pageable pageable = PageRequest.of(start/limit,limit);
        String privateId = userRepository.findUserByUserId(userId).orElseThrow(() -> new NotExistUserException()).getPrivateId();
        List<EventHistory> eventHistories = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth(), 23, 59);
        Page<EventForm> eventForms = eventFormRepository.findByPrivateIdAndEndTimeBetweenOrderByEndTime(privateId, startTime, endTime,pageable);
        for(EventForm ef : eventForms){
            Event event = eventRepository.findById(ef.getEventId()).orElseThrow(() -> new NotExistEventException());
            eventHistories.add(EventHistory.builder()
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
        List<EventHistory> eventHistories = new ArrayList<>();
        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(year, month, LocalDate.of(year, month, 1).lengthOfMonth(), 23, 59);
        List<EventForm> eventForms = eventFormRepository.findByPrivateIdAndEndTimeBetweenOrderByEndTimeDesc(privateId, startTime, endTime);
        return eventForms.size();
    }

    public EventTypeCount getEventTypeCount(String userId) {
        String privateId = userRepository.findUserByUserId(userId).orElseThrow(() -> new NotExistUserException()).getPrivateId();
        return EventTypeCount.builder()
                .Competition(eventFormRepository.findAllByPrivateIdAndEventType(privateId, EventType.COMPETITION).size())
                .Training(eventFormRepository.findAllByPrivateIdAndEventType(privateId, EventType.TRAINING).size())
                .build();
    }
}
