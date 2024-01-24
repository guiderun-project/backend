package com.guide.run.event.entity.service;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    @Transactional
    public EventCreatedResponse eventCreate(EventCreateRequest eventCreateRequest, String userId){
        Event createdEvent = eventRepository.save(Event.builder()
                .organizer(userId)
                .recruitStartDate(eventCreateRequest.getRecruitStartDate())
                .recruitEndDate(eventCreateRequest.getRecruitEndDate())
                .name(eventCreateRequest.getName())
                .isCreated(false)
                .isRecruited(eventCreateRequest.isRecruited())
                .type(eventCreateRequest.getType())
                .startTime(eventCreateRequest.getStartTime())
                .endTime(eventCreateRequest.getEndTime())
                .maxNumV(eventCreateRequest.getMaxNumV())
                .maxNumG(eventCreateRequest.getMaxNumG())
                .place(eventCreateRequest.getPlace())
                .content(eventCreateRequest.getContent()).build());
        return EventCreatedResponse.builder()
                .eventId(createdEvent.getId())
                .idCreated(createdEvent.isCreated())
                .build();
    }
}
