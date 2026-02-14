package com.guide.run.event.service;


import com.guide.run.attendance.entity.Attendance;
import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.attendance.service.AttendService;
import com.guide.run.event.entity.Comment;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventPopUpPartner;
import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.entity.dto.response.get.DetailEvent;
import com.guide.run.event.entity.dto.response.get.EventDetailPartner;
import com.guide.run.event.entity.dto.response.get.MyEventDdayResponse;
import com.guide.run.event.entity.repository.*;
import com.guide.run.event.entity.type.EventCategory;
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
import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.matching.repository.UnMatchingRepository;
import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;

    private final EventFormRepository eventFormRepository;

    private final UserRepository userRepository;

    private final MatchingRepository matchingRepository;
    private final TimeFormatter timeFormatter;
    private final UnMatchingRepository unMatchingRepository;
    private final AttendanceRepository attendanceRepository;
    private final EventCommentRepository eventCommentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final EventLikeRepository eventLikeRepository;

    private final AttendService attendService;


    @Transactional
    public EventCreatedResponse eventCreate(EventCreateRequest request, String privateId) {
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        EventCategory eventCategory;
        if(request.getEventCategory()==null){
            eventCategory = EventCategory.GENERAL;
        }else{
            eventCategory = request.getEventCategory();
        }

        EventRecruitStatus recruitStatus = EventRecruitStatus.RECRUIT_UPCOMING;
        EventStatus status = EventStatus.EVENT_UPCOMING;

        LocalDate today = LocalDate.now();

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
        if(request.getRecruitStartDate().isEqual(today)||request.getRecruitStartDate().isBefore(today)){
            recruitStatus = EventRecruitStatus.RECRUIT_OPEN;
        }


        Event createdEvent = eventRepository.save(Event.builder()
                .organizer(privateId)
                .recruitStartDate(request.getRecruitStartDate())
                .recruitEndDate(request.getRecruitEndDate())
                .name(request.getName())
                .recruitStatus(recruitStatus)
                .isApprove(true)
                .type(request.getEventType())
                .startTime(start)
                .endTime(end)
                .maxNumV(request.getMinNumV()) //todo:event 필드에 있는 것도 min으로 바꿔야 함...나중에...
                .maxNumG(request.getMinNumG())
                .place(request.getPlace())
                .status(status)
                .cityName(request.getCityName())
                .eventCategory(eventCategory)
                .content(request.getContent()).build());

        //자동 참가 처리
        eventFormRepository.save(
                EventForm.builder()
                        .privateId(privateId)
                        .eventId(createdEvent.getId())
                        .type(user.getType())
                        .hopeTeam(user.getRecordDegree())
                        .age(user.getAge())
                        .gender(user.getGender())
                        .isMatching(false)
                        .eventCategory(eventCategory)
                        .build()
        );
        unMatchingRepository.save(
                UnMatching.builder().eventId(createdEvent.getId())
                        .privateId(privateId).build()
        );
        attendanceRepository.save(
                Attendance.builder().eventId(createdEvent.getId())
                        .privateId(privateId)
                        .isAttend(true)
                        .date(createdEvent.getStartTime()).build()
        );

        return EventCreatedResponse.builder()
                .eventId(createdEvent.getId())
                .isApprove(createdEvent.isApprove())
                .build();
    }

    @Transactional
    public EventUpdatedResponse eventUpdate(EventCreateRequest request, String privateId, Long eventId) {
        userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        EventCategory eventCategory;
        if(request.getEventCategory()==null){
            eventCategory = EventCategory.GENERAL;
        }else{
            eventCategory = request.getEventCategory();
        }

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

        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        if (event.getOrganizer().equals(privateId)) {
            Event updatedEvent = eventRepository.save(Event.builder()
                    .id(eventId)
                    .organizer(privateId)
                    .recruitStartDate(request.getRecruitStartDate())
                    .recruitEndDate(request.getRecruitEndDate())
                    .name(request.getName())
                    .recruitStatus(event.getRecruitStatus())
                    .isApprove(event.isApprove())
                    .type(request.getEventType())
                    .startTime(timeFormatter.getDateTime(request.getDate(), request.getStartTime()))
                    .endTime(timeFormatter.getDateTime(request.getDate(), request.getEndTime()))
                    .maxNumV(request.getMinNumV()) //todo:event 필드에 있는 것도 min으로 바꿔야 함...나중에...
                    .maxNumG(request.getMinNumG())
                    .place(request.getPlace())
                    .status(event.getStatus())
                    .content(request.getContent())
                    .cityName(request.getCityName())
                    .eventCategory(eventCategory).build());

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
       userRepository.findUserByPrivateId(privateId).
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
            log.info("form deleted");
            matchingRepository.deleteAllByEventId(eventId);
            log.info("matching deleted");
            attendanceRepository.deleteAllByEventId(eventId);
            log.info("attendance deleted");
            unMatchingRepository.deleteAllByEventId(eventId);
            log.info("unMatching deleted");
            List<Comment> comments = eventCommentRepository.findAllByEventId(eventId);

            for(Comment c : comments){
                commentLikeRepository.deleteAllByCommentId(c.getCommentId());
            }
            log.info("commentLike deleted");
            eventCommentRepository.deleteAllByEventId(eventId);
            log.info("comment deleted");
            eventLikeRepository.deleteAllByEventId(eventId);
            log.info("eventLike deleted");
            eventRepository.deleteById(eventId);
            log.info("event deleted");
        } else
            throw new NotEventOrganizerException();
    }


    @Transactional
    public EventPopUpResponse eventPopUp(Long eventId, String privateId) {
        User user = userRepository.findUserByPrivateId(privateId).
                orElseThrow(NotExistUserException::new);

        Event event = eventRepository.findById(eventId).orElseThrow(
                NotExistEventException::new
        );
        
        //출석 인원 반영
        attendService.countAttendUser(eventId,true);

        //개최자 찾기
        User organizer = userRepository.findUserByPrivateId(event.getOrganizer()).orElse(null);

        String organizerId = null;
        String organizerRecord = null;
        UserType organizerType = UserType.GUIDE;
        String organizerName = null;

        Matching matching;
        List<EventPopUpPartner> partnerList = new ArrayList<>();

        boolean apply = false;
        boolean hasPartner = false;

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
                .partner(partnerList)
                .eventCategory(event.getEventCategory())
                .cityName(event.getCityName())
                .build();

        //매칭 여부로 파트너 정보 추가
        //신청 여부
        EventForm eventForm = eventFormRepository.findByEventIdAndPrivateId(eventId, privateId);
        if (eventForm != null) {
            //이벤트 신청서가 있을 때.
            apply = true;
                if (user.getType().equals(UserType.GUIDE)) {//가이드일 때
                    matching = matchingRepository.findByEventIdAndGuideId(eventId, user.getPrivateId());
                    if(matching!=null){
                        //매칭이 있을 때
                        User partner = userRepository.findUserByPrivateId(matching.getViId()).orElse(null);
                        hasPartner = true;

                        EventPopUpPartner partnerInfo = EventPopUpPartner.builder()
                                .partnerType(partner.getType())
                                .partnerName(partner.getName())
                                .partnerRecord(partner.getRecordDegree())
                                .build();
                        partnerList.add(partnerInfo);


                        response.setPartner(apply, hasPartner, partnerList);
                    }else{
                        hasPartner = false;
                        response.setPartner(apply, hasPartner, partnerList);
                    }

                } else { //vi일 때
                    List<Matching> matchings = matchingRepository.findAllByEventIdAndViId(eventId, user.getPrivateId());
                    if (matchings.size() == 0) {
                        //매칭이 없을 때
                        hasPartner = false;
                        response.setPartner(apply, hasPartner, partnerList);
                    } else {
                            //매칭이 있을 때
                            hasPartner = true;

                            for(Matching m : matchings){
                                User partner = userRepository.findUserByPrivateId(m.getGuideId()).orElseThrow(null);
                                EventPopUpPartner partnerInfo = EventPopUpPartner.builder()
                                        .partnerType(partner.getType())
                                        .partnerName(partner.getName())
                                        .partnerRecord(partner.getRecordDegree())
                                        .build();
                                partnerList.add(partnerInfo);
                            }

                            response.setPartner(apply, hasPartner, partnerList);
                    }
                }
            }

        return response;
    }

    public MyEventDdayResponse getMyEventDday(String privateId) {
        return MyEventDdayResponse.builder().eventItems(eventRepository.getMyEventDday(privateId)).build();
    }

    public DetailEvent getDetailEvent(Long eventId, String privateId) {
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        EventForm form = eventFormRepository.findByEventIdAndPrivateId(eventId, privateId);
        Event event = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        User organizer = userRepository.findUserByPrivateId(event.getOrganizer()).orElseThrow(NotExistUserException::new);

        //출석 인원 반영
        attendService.countAttendUser(eventId,true);

        DetailEvent detailEvent;


        List<EventDetailPartner> eventDetailPartnerList =new ArrayList<>();
        boolean isCheckOrganizer = false;
        if(organizer.getPrivateId().equals(privateId))
            isCheckOrganizer = true;
        Matching matching;
        //미신청한 경우
        if(form == null) {
            detailEvent = DetailEvent.builder()
                    .eventId(eventId)
                    .type(event.getType())
                    .name(event.getName())
                    .recruitStatus(event.getRecruitStatus())
                    .recruitStartDate(event.getRecruitStartDate())
                    .recruitEndDate(event.getRecruitEndDate())
                    .organizerId(organizer.getUserId())
                    .organizer(organizer.getName())
                    .organizerType(organizer.getType())
                    .organizerPace(organizer.getRecordDegree())
                    .date(event.getStartTime().toLocalDate().toString())
                    .startTime(event.getStartTime().toLocalTime().toString().substring(0,5))
                    .endTime(event.getEndTime().toLocalTime().toString().substring(0,5))
                    .created_at(LocalDate.from(event.getCreatedAt()))
                    .updated_at(LocalDate.from(event.getUpdatedAt()))
                    .place(event.getPlace())
                    .minNumV(event.getMaxNumV())
                    .minNumG(event.getMaxNumG())
                    .numG(event.getGuideCnt())
                    .numV(event.getViCnt())
                    .isApply(false)
                    .hasPartner(false)
                    .partner(eventDetailPartnerList)
                    .details(event.getContent())
                    .checkOrganizer(isCheckOrganizer)
                    .status(event.getStatus())
                    .eventCategory(event.getEventCategory())
                    .cityName(event.getCityName()).build();
        }
        //신청한 경우
        else{
            if(user.getType().equals(UserType.GUIDE)){
                matching = matchingRepository.findByEventIdAndGuideId(eventId, privateId);
                if(matching == null){
                    detailEvent = DetailEvent.builder()
                            .eventId(eventId)
                            .type(event.getType())
                            .name(event.getName())
                            .recruitStatus(event.getRecruitStatus())
                            .recruitStartDate(event.getRecruitStartDate())
                            .recruitEndDate(event.getRecruitEndDate())
                            .organizerId(organizer.getUserId())
                            .organizer(organizer.getName())
                            .organizerType(organizer.getType())
                            .organizerPace(organizer.getRecordDegree())
                            .date(event.getStartTime().toLocalDate().toString())
                            .startTime(event.getStartTime().toLocalTime().toString().substring(0,5))
                            .endTime(event.getEndTime().toLocalTime().toString().substring(0,5))
                            .created_at(LocalDate.from(event.getCreatedAt()))
                            .updated_at(LocalDate.from(event.getUpdatedAt()))
                            .place(event.getPlace())
                            .minNumV(event.getMaxNumV())
                            .minNumG(event.getMaxNumG())
                            .numG(event.getGuideCnt())
                            .numV(event.getViCnt())
                            .isApply(true)
                            .hasPartner(false)
                            .partner(eventDetailPartnerList)
                            .details(event.getContent())
                            .checkOrganizer(isCheckOrganizer)
                            .status(event.getStatus())
                            .eventCategory(event.getEventCategory())
                            .cityName(event.getCityName()).build();
                }
                else{
                    User vi = userRepository.findUserByPrivateId(matching.getViId()).orElseThrow(NotExistUserException::new);
                    eventDetailPartnerList.add(EventDetailPartner.builder()
                            .partnerType(vi.getType())
                            .partnerRecord(vi.getRecordDegree())
                            .partnerName(vi.getName())
                            .build());
                    detailEvent = DetailEvent.builder()
                            .eventId(eventId)
                            .type(event.getType())
                            .name(event.getName())
                            .recruitStatus(event.getRecruitStatus())
                            .recruitStartDate(event.getRecruitStartDate())
                            .recruitEndDate(event.getRecruitEndDate())
                            .organizerId(organizer.getUserId())
                            .organizer(organizer.getName())
                            .organizerType(organizer.getType())
                            .organizerPace(organizer.getRecordDegree())
                            .date(event.getStartTime().toLocalDate().toString())
                            .startTime(event.getStartTime().toLocalTime().toString().substring(0,5))
                            .endTime(event.getEndTime().toLocalTime().toString().substring(0,5))
                            .created_at(LocalDate.from(event.getCreatedAt()))
                            .updated_at(LocalDate.from(event.getUpdatedAt()))
                            .place(event.getPlace())
                            .minNumV(event.getMaxNumV())
                            .minNumG(event.getMaxNumG())
                            .numG(event.getGuideCnt())
                            .numV(event.getViCnt())
                            .isApply(true)
                            .hasPartner(true)
                            .partner(eventDetailPartnerList)
                            .details(event.getContent())
                            .checkOrganizer(isCheckOrganizer)
                            .status(event.getStatus())
                            .eventCategory(event.getEventCategory())
                            .cityName(event.getCityName()).build();
                }
            }else{
                List<Matching> matchingList = matchingRepository.findAllByEventIdAndViId(eventId, user.getPrivateId());
                if(matchingList.size()==0){
                    detailEvent = DetailEvent.builder()
                            .eventId(eventId)
                            .type(event.getType())
                            .name(event.getName())
                            .recruitStatus(event.getRecruitStatus())
                            .recruitStartDate(event.getRecruitStartDate())
                            .recruitEndDate(event.getRecruitEndDate())
                            .organizerId(organizer.getUserId())
                            .organizer(organizer.getName())
                            .organizerType(organizer.getType())
                            .organizerPace(organizer.getRecordDegree())
                            .date(event.getStartTime().toLocalDate().toString())
                            .startTime(event.getStartTime().toLocalTime().toString().substring(0,5))
                            .endTime(event.getEndTime().toLocalTime().toString().substring(0,5))
                            .created_at(LocalDate.from(event.getCreatedAt()))
                            .updated_at(LocalDate.from(event.getUpdatedAt()))
                            .place(event.getPlace())
                            .minNumV(event.getMaxNumV())
                            .minNumG(event.getMaxNumG())
                            .numG(event.getGuideCnt())
                            .numV(event.getViCnt())
                            .isApply(true)
                            .hasPartner(false)
                            .partner(eventDetailPartnerList)
                            .details(event.getContent())
                            .checkOrganizer(isCheckOrganizer)
                            .status(event.getStatus())
                            .eventCategory(event.getEventCategory())
                            .cityName(event.getCityName()).build();
                }
                else{
                    for(Matching m : matchingList){
                        User guide = userRepository.findUserByPrivateId(m.getGuideId()).orElseThrow(NotExistUserException::new);
                        eventDetailPartnerList.add(EventDetailPartner.builder()
                                .partnerType(guide.getType())
                                .partnerRecord(guide.getRecordDegree())
                                .partnerName(guide.getName())
                                .build());
                    }
                    detailEvent = DetailEvent.builder()
                            .eventId(eventId)
                            .type(event.getType())
                            .name(event.getName())
                            .recruitStatus(event.getRecruitStatus())
                            .recruitStartDate(event.getRecruitStartDate())
                            .recruitEndDate(event.getRecruitEndDate())
                            .organizerId(organizer.getUserId())
                            .organizer(organizer.getName())
                            .organizerType(organizer.getType())
                            .organizerPace(organizer.getRecordDegree())
                            .date(event.getStartTime().toLocalDate().toString())
                            .startTime(event.getStartTime().toLocalTime().toString().substring(0,5))
                            .endTime(event.getEndTime().toLocalTime().toString().substring(0,5))
                            .created_at(LocalDate.from(event.getCreatedAt()))
                            .updated_at(LocalDate.from(event.getUpdatedAt()))
                            .place(event.getPlace())
                            .minNumV(event.getMaxNumV())
                            .minNumG(event.getMaxNumG())
                            .numG(event.getGuideCnt())
                            .numV(event.getViCnt())
                            .isApply(true)
                            .hasPartner(true)
                            .partner(eventDetailPartnerList)
                            .details(event.getContent())
                            .checkOrganizer(isCheckOrganizer)
                            .status(event.getStatus())
                            .eventCategory(event.getEventCategory())
                            .cityName(event.getCityName()).build();
                }
            }
        }
        return detailEvent;
    }

}
