package com.guide.run.event.service;


import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.EventLike;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.entity.dto.response.get.DetailEvent;
import com.guide.run.event.entity.dto.response.get.MyEventDdayResponse;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventLikeRepository;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.dto.NotValidEventRecruitException;
import com.guide.run.global.exception.event.dto.NotValidEventStartException;
import com.guide.run.global.exception.event.logic.NotDeleteEventException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.sl.image.ImageHeaderBitmap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    private final EventFormRepository eventFormRepository;

    private final UserRepository userRepository;

    private final MatchingRepository matchingRepository;
    private final TimeFormatter timeFormatter;
    private final EventLikeRepository eventLikeRepository;

    private final AttendanceRepository attendanceRepository;
    @Transactional
    public EventCreatedResponse eventCreate(EventCreateRequest request, String privateId){
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        Event createdEvent = eventRepository.save(Event.builder()
                .organizer(privateId)
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .name(request.getTitle())
                .recruitStatus(EventRecruitStatus.RECRUIT_UPCOMING)
                .isApprove(true)
                .type(request.getEventType())
                .startTime(timeFormatter.getDateTime(request.getDate(), request.getStartTime()))
                .endTime(timeFormatter.getDateTime(request.getDate(), request.getEndTime()))
                .maxNumV(request.getMinNumV()) //todo:event 필드에 있는 것도 min으로 바꿔야 함...나중에...
                .maxNumG(request.getMinNumG())
                .place(request.getPlace())
                .status(EventStatus.EVENT_UPCOMING)
                .content(request.getContent()).build());
        eventLikeRepository.save(
                EventLike.builder()
                        .EventId(createdEvent.getId())
                        .privateIds(new ArrayList<>())
                        .build()
        );

        validEventTime(createdEvent);


        return EventCreatedResponse.builder()
                .eventId(createdEvent.getId())
                .isApprove(createdEvent.isApprove())
                .build();
    }

    @Transactional
    public EventUpdatedResponse eventUpdate(EventCreateRequest request, String privateId,Long eventId) {
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        if(event.getOrganizer().equals(privateId)){
            Event updatedEvent = eventRepository.save(Event.builder()
                    .id(eventId)
                    .organizer(privateId)
                    .recruitStartDate(request.getRecruitStartDate())
                    .recruitEndDate(request.getRecruitEndDate())
                    .name(request.getTitle())
                    .recruitStatus(event.getRecruitStatus())
                    .isApprove(event.isApprove())
                    .type(request.getEventType())
                    .startTime(timeFormatter.getDateTime(request.getDate(), request.getStartTime()))
                    .endTime(timeFormatter.getDateTime(request.getDate(), request.getEndTime()))
                    .maxNumV(request.getMinNumV()) //todo:event 필드에 있는 것도 min으로 바꿔야 함...나중에...
                    .maxNumG(request.getMinNumG())
                    .place(request.getPlace())
                    .content(request.getContent()).build());

            validEventTime(updatedEvent);

            return EventUpdatedResponse.builder()
                    .eventId(updatedEvent.getId())
                    .isApprove(updatedEvent.isApprove())
                    .build();

        }
        throw new NotEventOrganizerException();
    }

    //이벤트 모집 마감
    @Transactional
    public void eventClose(String privateId, Long eventId){
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);

        if(!event.getOrganizer().equals(privateId)){
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

        if(event.getStatus().equals(EventStatus.EVENT_END)){
            throw new NotDeleteEventException();
        }
        if(event.getOrganizer().equals(userId) || user.getRole().equals(Role.ROLE_ADMIN)){
            //todo : 매칭, 출석, 신청서, 파트너, 이벤트 좋아요, 댓글 전부 삭제해야 함.

            eventFormRepository.deleteAllByEventId(eventId);
            matchingRepository.deleteAllByEventId(eventId);
            attendanceRepository.deleteAllByEventId(eventId);
            eventLikeRepository.deleteById(eventId);
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

        boolean isMatching = false;
        //신청 여부
        EventForm eventForm = eventFormRepository.findByEventIdAndPrivateId(eventId, privateId);
        if(eventForm!=null){
            apply = true;
            isMatching = eventForm.isMatching();
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
                .hasPartner(isMatching) //파트너 존재 여부
                .partnerName(null) //파트너 이름
                .partnerRecord(null) //파트너 러닝등급
                .partnerType(null) //파트너 장애여부
                .build();

        //매칭 여부로 파트너 정보 추가
        Matching matching;
        String partnerId;
        if(isMatching){
            if(user.getType().equals(UserType.GUIDE)){
                matching = matchingRepository.findByEventIdAndGuideId(eventId, privateId);
                partnerId = matching.getViId();
            }else{
                matching = matchingRepository.findByEventIdAndViId(eventId, privateId);
                partnerId = matching.getGuideId();
            }

            User partner = userRepository.findUserByPrivateId(partnerId).orElseThrow(NotExistUserException::new);
            response.setPartner(eventForm.isMatching(),partner.getName(), partner.getRecordDegree(), partner.getType());
        }

        //이벤트 시작 당일 전까지는 파트너 공개 안함.
        if(LocalDate.now().isBefore(event.getStartTime().toLocalDate())){
            response.setPartner(false, null, null, null);
        }

        return response;
    }

    public MyEventDdayResponse getMyEventDday(String userId) {
        return MyEventDdayResponse.builder().eventItems(eventRepository.getMyEventDday(userId)).build();
    }

    public DetailEvent getDetailEvent(Long eventId, String userId) {
        return new DetailEvent();
    }

    public void validEventTime(Event event){
        if(event.getRecruitStartDate().isAfter(event.getRecruitEndDate())
                || event.getRecruitStartDate().isEqual(event.getRecruitEndDate())){
            throw new NotValidEventRecruitException();
        }

        if(event.getStartTime().isAfter(event.getEndTime())
                ||event.getStartTime().isEqual(event.getEndTime())){
            throw new NotValidEventStartException();
        }
    }

}
