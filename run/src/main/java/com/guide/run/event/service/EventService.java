package com.guide.run.event.service;

import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.request.EventUpdateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final TimeFormatter timeFormatter;
    @Transactional
    public EventCreatedResponse eventCreate(EventCreateRequest eventCreateRequest, String userId){
        Event createdEvent = eventRepository.save(Event.builder()
                .organizer(userId)
                .recruitStartDate(eventCreateRequest.getRecruitStartDate())
                .recruitEndDate(eventCreateRequest.getRecruitEndDate())
                .name(eventCreateRequest.getTitle())
                .recruitStatus(EventRecruitStatus.UPCOMING)
                .isApprove(false)
                .type(eventCreateRequest.getEventType())
                .startTime(eventCreateRequest.getStartTime())
                .endTime(eventCreateRequest.getEndTime())
                .maxNumV(eventCreateRequest.getMaxNumV())
                .maxNumG(eventCreateRequest.getMaxNumG())
                .place(eventCreateRequest.getPlace())
                .content(eventCreateRequest.getContent()).build());
        return EventCreatedResponse.builder()
                .eventId(createdEvent.getId())
                .isApprove(createdEvent.isApprove())
                .build();
    }

    @Transactional
    public EventUpdatedResponse eventUpdate(EventUpdateRequest eventUpdateRequest, String userId,Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        if(event.getOrganizer().equals(userId)){
            Event updatedEvent = eventRepository.save(Event.builder()
                    .id(eventId)
                    .organizer(userId)
                    .recruitStartDate(eventUpdateRequest.getRecruitStartDate())
                    .recruitEndDate(eventUpdateRequest.getRecruitEndDate())
                    .name(eventUpdateRequest.getTitle())
                    .recruitStatus(event.getRecruitStatus())
                    .isApprove(event.isApprove())
                    .type(eventUpdateRequest.getEventType())
                    .startTime(eventUpdateRequest.getStartTime())
                    .endTime(eventUpdateRequest.getEndTime())
                    .maxNumV(eventUpdateRequest.getMaxNumV())
                    .maxNumG(eventUpdateRequest.getMaxNumG())
                    .place(eventUpdateRequest.getPlace())
                    .content(eventUpdateRequest.getContent()).build());
            return EventUpdatedResponse.builder()
                    .eventId(updatedEvent.getId())
                    .isApprove(updatedEvent.isApprove())
                    .build();
        }
        throw new NotEventOrganizerException();
    }
    @Transactional
    public void eventDelete(String userId,Long eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        if(event.getOrganizer().equals(userId)){
            eventRepository.deleteById(eventId);
        }
        else
            throw new NotEventOrganizerException();
    }

    @Transactional
    public EventPopUpResponse eventPopUp(Long eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(
                NotExistEventException::new
        );

        EventPopUpResponse response = EventPopUpResponse.builder()
                .eventId(event.getId())
                .type(event.getType())
                .name(event.getName())
                .recruitStatus(event.getRecruitStatus())
                .date(LocalDate.from(event.getStartTime()))
                .startTime(timeFormatter.getHHMM(event.getStartTime()))
                .endTime(timeFormatter.getHHMM(event.getEndTime()))
                .viCnt(event.getViCnt())
                .guideCnt(event.getGuideCnt())
                .place(event.getPlace())
                .content(event.getContent())
                .updatedAt(LocalDate.from(event.getUpdatedAt()))
                .build();

        return response;
    }
}
