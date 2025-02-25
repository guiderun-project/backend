package com.guide.run.partner.service;

import com.guide.run.attendance.entity.Attendance;
import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.resource.NotExistEventException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.PartnerLike;
import com.guide.run.partner.entity.partner.repository.PartnerLikeRepository;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final PartnerLikeRepository partnerLikeRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final MatchingRepository matchingRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public void partnerLike(String userId, String privateId){
        User user = userRepository.findUserByPrivateId(privateId).orElseThrow(NotExistUserException::new);
        User partner = userRepository.findUserByUserId(userId).orElseThrow(RuntimeException::new); //todo : 존재하지 않는 파트너 에러 추가 필요
        PartnerLike partnerLike = partnerLikeRepository.findByRecIdAndSendId(partner.getPrivateId(),privateId).orElse(null);

        if(partnerLike!=null){ //이미 있으면 취소
            partnerLikeRepository.delete(partnerLike);
        }else{//없을 시 생성
            partnerLikeRepository.save(PartnerLike.builder()
                    .recId(partner.getPrivateId())
                    .sendId(privateId)
                    .build());
        }

    }


    @Transactional
    public void setAttendViPartnerList(long eventId, User vi){
        log.info("setAttendPartnerList - privateId : {}", vi.getPrivateId());
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        //매칭된 회원 조회
        List<Matching> matchings = matchingRepository.findAllByEventIdAndViId(eventId, vi.getPrivateId());
        for (Matching matching : matchings) {

            Attendance attendance = attendanceRepository.findByEventIdAndPrivateId(eventId, matching.getGuideId());

            if(attendance==null){
                continue;
            }

            //출석한 파트너일 경우
            if(attendance.isAttend()){
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
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);
        //매칭된 회원 조회
        Matching matching = matchingRepository.findByEventIdAndGuideId(eventId, guide.getPrivateId());

        if(matching!=null){
            Attendance attendance = attendanceRepository.findByEventIdAndPrivateId(eventId, matching.getGuideId());

            if(attendance==null){
                return;
            }

            //vi가 출석했다면 파트너 추가
            if(attendance.isAttend()){
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
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);

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
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistEventException::new);

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
        } else {
            log.info("파트너에 이미 해당 이벤트가 존재함 - eventId: {}", eventId);
        }

        partnerRepository.save(partner);
    }

    @Transactional
    public void removeEventPartner(Partner partner, Long eventId, boolean isTraining) {
        try {
            Set<Long> eventIds = isTraining ? partner.getTrainingIds() : partner.getContestIds();
            if (eventIds.contains(eventId)) {
                if (isTraining) {
                    partner.removeTraining(eventId);
                    log.info("트레이닝 파트너 삭제 - eventId: {}", eventId);
                } else {
                    partner.removeContest(eventId);
                    log.info("대회 파트너 삭제 - eventId: {}", eventId);
                }
            } else {
                log.info("파트너에 해당 이벤트가 존재하지 않음 : {}", eventId);
            }

            partnerRepository.save(partner);

        }catch (Exception e){
            log.error("파트너 저장 중 오류 발생 - eventId: {}, error: {}", eventId, e.getMessage());
            throw new RuntimeException("파트너 저장 실패", e);
        }
    }

}
