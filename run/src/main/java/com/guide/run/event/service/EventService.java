package com.guide.run.event.service;


import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.request.EventUpdateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    private final EventFormRepository eventFormRepository;

    private final UserRepository userRepository;

    private final MatchingRepository matchingRepository;
    private final TimeFormatter timeFormatter;
    @Transactional
    public EventCreatedResponse eventCreate(EventCreateRequest eventCreateRequest, String userId){
        User user = userRepository.findUserByPrivateId(userId).
                orElseThrow(NotExistUserException::new);

        Event createdEvent = eventRepository.save(Event.builder()
                .organizer(userId)
                .recruitStartDate(eventCreateRequest.getRecruitStartDate())
                .recruitEndDate(eventCreateRequest.getRecruitEndDate())
                .name(eventCreateRequest.getTitle())
                .recruitStatus(EventRecruitStatus.RECRUIT_UPCOMING)
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
        User user = userRepository.findUserByPrivateId(userId).
                orElseThrow(NotExistUserException::new);

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

    //이벤트 모집 마감
    @Transactional
    public void eventClose(String userId, Long eventId){
        User user = userRepository.findUserByPrivateId(userId).
                orElseThrow(NotExistUserException::new);

        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);

        if(!event.getOrganizer().equals(userId)){
            throw new NotEventOrganizerException();
        }
        //지금 시간 긁어서 이벤트 마감 시간으로 변경 + 이벤트 상태 변경.
        event.closeEvent();
        eventRepository.save(event);
    }


    @Transactional
    public void eventDelete(String userId,Long eventId){
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User user = userRepository.findUserByPrivateId(userId).orElseThrow(NotExistUserException::new);

        if(event.getOrganizer().equals(userId) || user.getRole().equals(Role.ROLE_ADMIN)){
            eventRepository.deleteById(eventId);
        }
        else
            throw new NotEventOrganizerException();
    }

    //todo : 파트너 정보 팝업에 추가
    @Transactional
    public EventPopUpResponse eventPopUp(Long eventId, String privateId){
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        Event event = eventRepository.findById(eventId).orElseThrow(
                NotExistEventException::new
        );

        boolean apply = false;
        //신청 여부
        EventForm eventForm = eventFormRepository.findByEventIdAndPrivateId(eventId, privateId);
        if(eventForm!=null){
            apply = true;
        }


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
                .isApply(apply)
                //todo : 2차에서 추가된 부분
                .hasPartner(eventForm.isMatching()) //파트너 존재 여부
                .partnerName(null) //파트너 이름
                .partnerRecord(null) //파트너 러닝등급
                .partnerType(null) //파트너 장애여부
                .build();

        //매칭 여부로 파트너 정보 추가
        Matching matching;
        String partnerId;
        if(eventForm.isMatching()){
            if(user.getType().equals(UserType.GUIDE)){
                matching = matchingRepository.findByEventIdAndGuideId(eventId, privateId);
                partnerId = matching.getViId();
            }else{
                matching = matchingRepository.findByEventIdAndViId(eventId, privateId);
                partnerId = matching.getGuideId();
            }

            User partner = userRepository.findUserByPrivateId(partnerId).orElseThrow(NotExistUserException::new);
            response.setPartner(partner.getName(), partner.getRecordDegree(), partner.getType());
        }

        return response;
    }
}
