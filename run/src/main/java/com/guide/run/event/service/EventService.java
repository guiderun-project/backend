package com.guide.run.event.service;


import com.guide.run.event.entity.Comment;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.EventLike;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.entity.dto.response.get.DetailEvent;
import com.guide.run.event.entity.dto.response.get.MyEventDdayResponse;
import com.guide.run.event.entity.repository.*;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.converter.TimeFormatter;
import com.guide.run.global.exception.event.authorize.NotEventOrganizerException;
import com.guide.run.global.exception.event.dto.NotValidEventRecruitException;
import com.guide.run.global.exception.event.dto.NotValidEventStartException;
import com.guide.run.global.exception.event.logic.NotDeleteEventException;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    private final EventFormRepository eventFormRepository;

    private final UserRepository userRepository;

    private final MatchingRepository matchingRepository;
    private final TimeFormatter timeFormatter;
    private final EventLikeRepository eventLikeRepository;
    private final UnMatchingRepository unMatchingRepository;
    private final AttendanceRepository attendanceRepository;
    private final EventCommentRepository eventCommentRepository;
    private final CommentLikeRepository commentLikeRepository;


    @Transactional
    public EventCreatedResponse eventCreate(EventCreateRequest request, String privateId) {
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        EventRecruitStatus recruitStatus = EventRecruitStatus.RECRUIT_UPCOMING;
        EventStatus status = EventStatus.EVENT_UPCOMING;

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = timeFormatter.getDateTime(request.getDate(), request.getStartTime());
        LocalDateTime end = timeFormatter.getDateTime(request.getDate(), request.getEndTime());

        if(request.getRecruitStartDate().isAfter(request.getRecruitEndDate())){
            throw new NotValidEventRecruitException();
        }

        if(request.getRecruitStartDate().isAfter(start.toLocalDate())){
            throw new NotValidEventStartException();
        }

        if(start.isAfter(end) || start.isEqual(end)){
            throw new NotValidEventStartException();
        }

        //오늘이면 오픈
        if(request.getRecruitStartDate().isEqual(today)||request.getRecruitStartDate().isAfter(today)){
            recruitStatus = EventRecruitStatus.RECRUIT_OPEN;
        }

        //마감기간 지났으면 마감처리
        if(request.getRecruitEndDate().isAfter(today)){
            recruitStatus = EventRecruitStatus.RECRUIT_CLOSE;
        }

        if(start.isEqual(now)||start.isAfter(now)){
            recruitStatus = EventRecruitStatus.RECRUIT_CLOSE;
            status = EventStatus.EVENT_OPEN;
        }


        Event createdEvent = eventRepository.save(Event.builder()
                .organizer(privateId)
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .name(request.getName())
                .recruitStatus(recruitStatus)
                .isApprove(true)
                .type(request.getEventType())
                .startTime(timeFormatter.getDateTime(request.getDate(), request.getStartTime()))
                .endTime(timeFormatter.getDateTime(request.getDate(), request.getEndTime()))
                .maxNumV(request.getMinNumV()) //todo:event 필드에 있는 것도 min으로 바꿔야 함...나중에...
                .maxNumG(request.getMinNumG())
                .place(request.getPlace())
                .status(status)
                .content(request.getContent()).build());

        //자동 참가 처리
        EventForm eventForm = eventFormRepository.save(
                EventForm.builder()
                        .privateId(privateId)
                        .eventId(createdEvent.getId())
                        .type(user.getType())
                        .age(user.getAge())
                        .gender(user.getGender())
                        .isMatching(false)
                        .build()
        );

        return EventCreatedResponse.builder()
                .eventId(createdEvent.getId())
                .isApprove(createdEvent.isApprove())
                .build();
    }

    @Transactional
    public EventUpdatedResponse eventUpdate(EventCreateRequest request, String privateId, Long eventId) {
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        EventRecruitStatus recruitStatus = EventRecruitStatus.RECRUIT_UPCOMING;
        EventStatus status = EventStatus.EVENT_UPCOMING;

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime start = timeFormatter.getDateTime(request.getDate(), request.getStartTime());
        LocalDateTime end = timeFormatter.getDateTime(request.getDate(), request.getEndTime());

        if(request.getRecruitStartDate().isAfter(request.getRecruitEndDate())){
            throw new NotValidEventRecruitException();
        }

        if(request.getRecruitStartDate().isAfter(start.toLocalDate())){
            throw new NotValidEventStartException();
        }

        if(start.isAfter(end) || start.isEqual(end)){
            throw new NotValidEventStartException();
        }

        //오늘이면 오픈
        if(request.getRecruitStartDate().isEqual(today)||request.getRecruitStartDate().isAfter(today)){
            recruitStatus = EventRecruitStatus.RECRUIT_OPEN;
        }

        //마감기간 지났으면 마감처리
        if(request.getRecruitEndDate().isAfter(today)){
            recruitStatus = EventRecruitStatus.RECRUIT_CLOSE;
        }

        if(start.isEqual(now)||start.isAfter(now)){
            recruitStatus = EventRecruitStatus.RECRUIT_CLOSE;
            status = EventStatus.EVENT_OPEN;
        }

        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        if (event.getOrganizer().equals(privateId)) {
            Event updatedEvent = eventRepository.save(Event.builder()
                    .id(eventId)
                    .organizer(privateId)
                    .recruitStartDate(request.getRecruitStartDate())
                    .recruitEndDate(request.getRecruitEndDate())
                    .name(request.getName())
                    .recruitStatus(recruitStatus)
                    .isApprove(event.isApprove())
                    .type(request.getEventType())
                    .startTime(timeFormatter.getDateTime(request.getDate(), request.getStartTime()))
                    .endTime(timeFormatter.getDateTime(request.getDate(), request.getEndTime()))
                    .maxNumV(request.getMinNumV()) //todo:event 필드에 있는 것도 min으로 바꿔야 함...나중에...
                    .maxNumG(request.getMinNumG())
                    .place(request.getPlace())
                    .status(status)
                    .content(request.getContent()).build());

            if(request.getRecruitStartDate().isBefore(today)|| request.getRecruitStartDate().isEqual(today)){
                eventClose(privateId, updatedEvent.getId());
            }

            return EventUpdatedResponse.builder()
                    .eventId(updatedEvent.getId())
                    .isApprove(updatedEvent.isApprove())
                    .build();
        }
        throw new NotEventOrganizerException();
    }

    //이벤트 모집 마감
    @Transactional
    public void eventClose(String privateId, Long eventId) {
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);

        if (!event.getOrganizer().equals(privateId)) {
            throw new NotEventOrganizerException();
        }
        //지금 시간 긁어서 이벤트 마감 시간으로 변경 + 이벤트 상태 변경.
        event.closeEvent();
        eventRepository.save(event);
    }


    @Transactional
    public void eventDelete(String userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User user = userRepository.findUserByPrivateId(userId).orElseThrow(NotExistUserException::new);
        LocalDateTime now = LocalDateTime.now();
        if(event.getEndTime().isBefore(now) ||event.getEndTime().isEqual(now)){
            throw new NotDeleteEventException(); //종료된 이벤트는 삭제 못함.
        }

        if (event.getOrganizer().equals(userId) || user.getRole().equals(Role.ROLE_ADMIN)) {
            //todo : 매칭, 출석, 신청서, 이벤트 좋아요, 댓글, 댓글 좋아요 전부 삭제해야 함. 파트너는 종료됐을 때 추가되기 때문에 삭제 안해도 됨.
            eventFormRepository.deleteAllByEventId(eventId);
            matchingRepository.deleteAllByEventId(eventId);
            attendanceRepository.deleteAllByEventId(eventId);
            unMatchingRepository.deleteAllByEventId(eventId);
            List<Comment> comments = eventCommentRepository.findAllByEventId(eventId);
            
            for(Comment c : comments){
                commentLikeRepository.deleteAllByCommentId(c.getCommentId());
            }
            
            eventRepository.deleteById(eventId);

        } else
            throw new NotEventOrganizerException();
    }

    //todo : 파트너 정보 팝업에 추가
    @Transactional
    public EventPopUpResponse eventPopUp(Long eventId, String privateId) {
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        Event event = eventRepository.findById(eventId).orElseThrow(
                NotExistEventException::new
        );

        User organizer = userRepository.findUserByPrivateId(event.getOrganizer()).orElse(null);

        String organizerId = null;
        String organizerRecord = null;
        UserType organizerType = UserType.GUIDE;
        String organizerName = null;

        Matching matching;
        String partnerId;

        boolean apply = false;

        if(organizer!=null){
            organizerId = organizer.getUserId();
            organizerName = organizer.getName();
            organizerRecord = organizer.getRecordDegree();
            organizerType = organizer.getType();
        }


        EventPopUpResponse response = EventPopUpResponse.builder()
                .eventId(event.getId())
                .type(event.getType())
                .name(event.getName())
                .organizerId(organizerId)
                .organizer(organizerName)
                .organizerRecord(organizerRecord)
                .organizerType(organizerType)
                .recruitStatus(event.getRecruitStatus())
                .date(LocalDate.from(event.getStartTime()))
                .startTime(timeFormatter.getHHMM(event.getStartTime()))
                .endTime(timeFormatter.getHHMM(event.getEndTime()))
                .recruitVi(event.getMaxNumV())
                .recruitGuide(event.getMaxNumG())
                .viCnt(event.getViCnt())
                .guideCnt(event.getGuideCnt())
                .place(event.getPlace())
                .status(event.getStatus())
                .content(event.getContent())
                .updatedAt(LocalDate.from(event.getUpdatedAt()))
                .isApply(apply)
                //todo : 2차에서 추가된 부분
                .hasPartner(false) //파트너 존재 여부
                .partnerName(null) //파트너 이름
                .partnerRecord(null) //파트너 러닝등급
                .partnerType(null) //파트너 장애여부
                .build();

        //매칭 여부로 파트너 정보 추가
        //신청 여부
        EventForm eventForm = eventFormRepository.findByEventIdAndPrivateId(eventId, privateId);
        if (eventForm != null) {
            apply = true;
            if (eventForm.isMatching()) {
                if (user.getType().equals(UserType.GUIDE)) {
                    matching = matchingRepository.findByEventIdAndGuideId(eventId, privateId);
                    if(matching!=null){
                        partnerId = matching.getViId();
                        User partner = userRepository.findUserByPrivateId(partnerId).orElse(null);
                        response.setPartner(apply, eventForm.isMatching(), partner.getName(), partner.getRecordDegree(), partner.getType());
                    }

                } else {
                    matching = matchingRepository.findByEventIdAndViId(eventId, privateId);
                    if(matching!=null){
                        partnerId = matching.getGuideId();
                        User partner = userRepository.findUserByPrivateId(partnerId).orElseThrow(null);
                        response.setPartner(apply, eventForm.isMatching(), partner.getName(), partner.getRecordDegree(), partner.getType());
                    }
                }
            }
        }else{
            apply = false;
        }


        //이벤트 시작 당일 전까지는 파트너 공개 안함.
        if (LocalDate.now().isBefore(event.getStartTime().toLocalDate())) {
            response.setPartner(apply, false, null, null, null);
        }

        return response;
    }

    public MyEventDdayResponse getMyEventDday(String userId) {
        return MyEventDdayResponse.builder().eventItems(eventRepository.getMyEventDday(userId)).build();
    }

    public DetailEvent getDetailEvent(Long eventId, String privateId) {
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        EventForm form = eventFormRepository.findByEventIdAndPrivateId(eventId, privateId);
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User organizer = userRepository.findUserByUserId(event.getOrganizer()).orElseThrow(NotExistUserException::new);
        DetailEvent detailEvent;

        boolean isCheckOrganizer = false;
        if(organizer.getPrivateId().equals(privateId))
            isCheckOrganizer = true;
        Matching matching;
        //미신청한 경우
        if(form == null) {
            detailEvent = new DetailEvent(
                    eventId,event.getType(),event.getName(),event.getRecruitStatus(),
                    organizer.getUserId(),
                    organizer.getName(),organizer.getType(),
                    organizer.getRecordDegree(),
                    event.getStartTime().getYear()+"."+event.getStartTime().getMonthValue()+"."+event.getStartTime().getDayOfMonth(),
                    event.getStartTime().toLocalTime().toString().substring(0,5),
                    event.getEndTime().toLocalTime().toString().substring(0,5),
                    event.getCreatedAt(),event.getUpdatedAt(),event.getPlace(),
                    event.getMaxNumV(),event.getMaxNumG(), //모집 인원
                    event.getViCnt(),event.getGuideCnt(), //실제 참여 인원
                    null,null,null,
                    event.getContent(),isCheckOrganizer,false,event.getStatus()
            );
        }
        //신청한 경우
        else{
            if(user.getType().equals(UserType.GUIDE)){
                matching = matchingRepository.findByEventIdAndGuideId(eventId, privateId);
                if(matching == null){
                    detailEvent = new DetailEvent(
                        eventId,event.getType(),event.getName(),event.getRecruitStatus(),
                            organizer.getUserId(),
                            organizer.getName(),organizer.getType(),
                        organizer.getRecordDegree(),
                            event.getStartTime().getYear()+"."+event.getStartTime().getMonthValue()+"."+event.getStartTime().getDayOfMonth(),
                            event.getStartTime().toLocalTime().toString().substring(0,5),
                            event.getEndTime().toLocalTime().toString().substring(0,5),
                        event.getCreatedAt(),event.getUpdatedAt(),event.getPlace(),
                            event.getMaxNumV(),event.getMaxNumG(), //모집 인원
                            event.getViCnt(),event.getGuideCnt(), //실제 참여 인원
                        null,null,null,
                        event.getContent(),isCheckOrganizer,true,event.getStatus());
                }
                else{
                    User vi = userRepository.findUserByUserId(matching.getViId()).orElseThrow(NotExistUserException::new);
                    detailEvent = new DetailEvent(
                            eventId,event.getType(),event.getName(),event.getRecruitStatus(),
                            organizer.getUserId(),
                            organizer.getName(),organizer.getType(),
                            organizer.getRecordDegree(),
                            event.getStartTime().getYear()+"."+event.getStartTime().getMonthValue()+"."+event.getStartTime().getDayOfMonth(),
                            event.getStartTime().toLocalTime().toString().substring(0,5),
                            event.getEndTime().toLocalTime().toString().substring(0,5),
                            event.getCreatedAt(),event.getUpdatedAt(),event.getPlace(),
                            event.getMaxNumV(),event.getMaxNumG(), //모집 인원
                            event.getViCnt(),event.getGuideCnt(), //실제 참여 인원
                            vi.getName(), vi.getType(), vi.getRecordDegree(),
                            event.getContent(),isCheckOrganizer,true,event.getStatus());
                }
            }else{
                matching = matchingRepository.findAllByEventIdAndViId(eventId, user.getUserId()).get(0);
                if(matching == null){
                    detailEvent = new DetailEvent(
                            eventId,event.getType(),event.getName(),event.getRecruitStatus(),
                            organizer.getUserId(),
                            organizer.getName(),organizer.getType(),
                            organizer.getRecordDegree(),
                            event.getStartTime().getYear()+"."+event.getStartTime().getMonthValue()+"."+event.getStartTime().getDayOfMonth(),
                            event.getStartTime().toLocalTime().toString().substring(0,5),
                            event.getEndTime().toLocalTime().toString().substring(0,5),
                            event.getCreatedAt(),event.getUpdatedAt(),event.getPlace(),
                            event.getMaxNumV(),event.getMaxNumG(), //모집 인원
                            event.getViCnt(),event.getGuideCnt(), //실제 참여 인원
                            null,null,null,
                            event.getContent(),isCheckOrganizer,true,event.getStatus());
                }
                else{
                    User guide = userRepository.findUserByUserId(matching.getGuideId()).orElseThrow(NotExistUserException::new);
                    detailEvent = new DetailEvent(
                            eventId,event.getType(),event.getName(),event.getRecruitStatus(),
                            organizer.getUserId(),
                            organizer.getName(),organizer.getType(),
                            organizer.getRecordDegree(),
                            event.getStartTime().getYear()+"."+event.getStartTime().getMonthValue()+"."+event.getStartTime().getDayOfMonth(),
                            event.getStartTime().toLocalTime().toString().substring(0,5),
                            event.getEndTime().toLocalTime().toString().substring(0,5),
                            event.getCreatedAt(),event.getUpdatedAt(),event.getPlace(),
                            event.getMaxNumV(),event.getMaxNumG(), //모집 인원
                            event.getViCnt(),event.getGuideCnt(), //실제 참여 인원
                            guide.getName(), guide.getType(), guide.getRecordDegree(),
                            event.getContent(),isCheckOrganizer,true,event.getStatus());
                }
            }
        }


        return detailEvent;
    }
}
