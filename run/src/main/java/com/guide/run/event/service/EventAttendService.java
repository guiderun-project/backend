package com.guide.run.event.service;

import com.guide.run.attendance.service.AttendService;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.dto.response.attend.AttendCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationCount;
import com.guide.run.event.entity.dto.response.attend.ParticipationInfos;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.attendance.entity.Attendance;
import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.regex.Match;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventAttendService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final MatchingRepository matchingRepository;
    private final EventRepository eventRepository;
    private final AttendService attendService;

    public void requestAttend(Long eventId, String userId) {
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        Attendance attendance = attendanceRepository.findByEventIdAndPrivateId(eventId, user.getPrivateId());
        if(attendance.isAttend()){
            if(user.getType().equals(UserType.VI)){
                setNotAttendViPartnerList(eventId, user);
            }else if(user.getType().equals(UserType.GUIDE)){
                setNotAttendGuidePartner(eventId, user);
            }
            attendanceRepository.save(
                    Attendance.builder()
                            .eventId(eventId)
                            .privateId(user.getPrivateId())
                            .isAttend(false)
                            .date(LocalDateTime.now())
                            .build()
            );

        }
        else{
            if(user.getType().equals(UserType.VI)){
                setAttendViPartnerList(eventId, user);
            }else if(user.getType().equals(UserType.GUIDE)){
                setAttendGuidePartner(eventId, user);
            }
            attendanceRepository.save(
                    Attendance.builder()
                            .eventId(eventId)
                            .privateId(user.getPrivateId())
                            .isAttend(true)
                            .date(LocalDateTime.now())
                            .build()
            );
        }

        //출석 개수 반영
        attendService.countAttendEvent(user.getPrivateId());

    }

    public AttendCount getAttendCount(Long eventId) {
        return AttendCount.builder()
                .attend(attendanceRepository.countByIsAttendAndEventId(true,eventId))
                .notAttend(attendanceRepository.countByIsAttendAndEventId(false,eventId))
                .build();
    }

    public ParticipationCount getParticipationCount(Long eventId) {
        Long guideNum = attendanceRepository.countUserType(eventId, UserType.GUIDE);
        Long ViNum = attendanceRepository.countUserType(eventId, UserType.VI);
        return ParticipationCount.builder()
                .count(guideNum+ViNum)
                .vi(ViNum)
                .guide(guideNum)
                .build();
    }

    public ParticipationInfos getParticipationInfos(Long eventId) {
        return ParticipationInfos.builder()
                .attend(attendanceRepository.getParticipationInfo(eventId,true))
                .notAttend(attendanceRepository.getParticipationInfo(eventId,false))
                .build();
    }


    @Transactional
    public void setAttendViPartnerList(long eventId, User vi){
        log.info("setAttendPartnerList - privateId : {}", vi.getPrivateId());
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistUserException::new);
        //매칭된 회원 조회
        List<Matching> matchings = matchingRepository.findAllByEventIdAndViId(eventId, vi.getPrivateId());
        for (Matching matching : matchings) {

            boolean isAttend = attendanceRepository.findByEventIdAndPrivateId(eventId, matching.getGuideId()).isAttend();

            //출석한 파트너일 경우
            if(isAttend){
                // 파트너 조회. 존재하지 않으면 신규 생성
                Partner partner = partnerRepository.findByViIdAndGuideId(matching.getViId(), matching.getGuideId())
                        .orElseGet(() -> Partner.builder()
                                .viId(matching.getViId())
                                .guideId(matching.getGuideId())
                                .contestIds(new HashSet<>())
                                .trainingIds(new HashSet<>())
                                .build());

                boolean isTraining = EventType.TRAINING.equals(e.getType());
                addEventPartner(partner, matching.getEventId(), isTraining);
            }

        }

    }

    @Transactional
    public void setAttendGuidePartner(long eventId, User guide){
        log.info("setAttendGuidePartner - privateId : {}", guide.getPrivateId());
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistUserException::new);
        //매칭된 회원 조회
        Matching matching = matchingRepository.findByEventIdAndGuideId(eventId, guide.getPrivateId());

        if(matching!=null){
            boolean isAttend = attendanceRepository.findByEventIdAndPrivateId(eventId, matching.getViId()).isAttend();

            //vi가 출석했다면 파트너 추가
            if(isAttend){
                // 파트너 조회. 존재하지 않으면 신규 생성
                Partner partner = partnerRepository.findByViIdAndGuideId(matching.getViId(), matching.getGuideId())
                        .orElseGet(() -> Partner.builder()
                                .viId(matching.getViId())
                                .guideId(matching.getGuideId())
                                .contestIds(new HashSet<>())
                                .trainingIds(new HashSet<>())
                                .build());

                boolean isTraining = EventType.TRAINING.equals(e.getType());
                addEventPartner(partner, matching.getEventId(), isTraining);
            }
        }

    }

    @Transactional
    public void setNotAttendViPartnerList(long eventId, User vi){
        log.info("setNotAttendPartnerList - privateId : {}", vi.getPrivateId());
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistUserException::new);

        //매칭된 회원 조회
        List<Matching> matchings = matchingRepository.findAllByEventIdAndViId(eventId, vi.getPrivateId());
        for (Matching matching : matchings) {
            // 파트너 조회 후 해당 이벤트가 있으면 삭제.
            Partner partner = partnerRepository.findByViIdAndGuideId(matching.getViId(), matching.getGuideId()).orElse(null);

            if(partner!=null){
                boolean isTraining = EventType.TRAINING.equals(e.getType());
                removeEventPartner(partner, matching.getEventId(), isTraining);
            }
        }

    }

    @Transactional
    public void setNotAttendGuidePartner(long eventId, User guide){
        log.info("notAttendGuidePartner - privateId : {}", guide.getPrivateId());
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistUserException::new);

        Matching matching = matchingRepository.findByEventIdAndGuideId(eventId, guide.getPrivateId());
        if(matching!=null){
            // 파트너 조회 후 해당 이벤트가 있으면 삭제.
            Partner partner = partnerRepository.findByViIdAndGuideId(matching.getViId(), matching.getGuideId()).orElse(null);

            if(partner!=null){
                boolean isTraining = EventType.TRAINING.equals(e.getType());
                removeEventPartner(partner, matching.getEventId(), isTraining);
            }
        }
    }

    @Transactional
    public void addEventPartner(Partner partner, Long eventId, boolean isTraining) {
        Set<Long> eventIds = isTraining ? partner.getTrainingIds() : partner.getContestIds();

        if (!eventIds.contains(eventId)) {
            if (isTraining) {
                partner.addTraining(eventId);
                log.info("트레이닝 파트너 저장 - eventId: {}", eventId);
            } else {
                partner.addContest(eventId);
                log.info("대회 파트너 저장 - eventId: {}", eventId);
            }
            partnerRepository.save(partner);
        } else {
            log.info("파트너에 이미 해당 이벤트가 존재함 - eventId: {}", eventId);
        }

        partnerRepository.save(partner);
    }

    @Transactional
    public void removeEventPartner(Partner partner, Long eventId, boolean isTraining) {
     Set<Long> eventIds = isTraining ? partner.getTrainingIds() : partner.getContestIds();

     if (eventIds.contains(eventId)) {
         if(isTraining){
             partner.removeTraining(eventId);
             log.info("트레이닝 파트너 삭제 - eventId: {}", eventId);
         }else{
             partner.removeContest(eventId);
             log.info("대회 파트너 삭제 - eventId: {}", eventId);
         }
     }else{
         log.info("파트너에 해당 이벤트가 존재하지 않음 : {}", eventId);
     }

     partnerRepository.save(partner);

    }

}
