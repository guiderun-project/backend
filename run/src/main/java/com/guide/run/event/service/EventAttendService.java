package com.guide.run.event.service;

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
import com.guide.run.temp.member.entity.Attendance;
import com.guide.run.temp.member.repository.AttendanceRepository;
import com.guide.run.user.entity.type.UserType;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventAttendService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final MatchingRepository matchingRepository;
    private final EventRepository eventRepository;

    public void requestAttend(Long eventId, String userId) {
        User user = userRepository.findUserByUserId(userId).orElseThrow(NotExistUserException::new);
        Attendance attendance = attendanceRepository.findByEventIdAndPrivateId(eventId, user.getPrivateId());
        if(attendance.isAttend()){
            if(user.getType().equals(UserType.VI)){
                setAttendPartnerList(eventId, user);
            }
            attendanceRepository.save(
                    Attendance.builder()
                            .eventId(eventId)
                            .privateId(user.getPrivateId())
                            .isAttend(false)
                            .date(null)
                            .build()
            );

        }
        else{
            if(user.getType().equals(UserType.VI)){
                setNotAttendPartnerList(eventId, user);
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
    public void setAttendPartnerList(long eventId, User vi){
        log.info("setAttendPartnerList - privateId : {}", vi.getPrivateId());
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistUserException::new);

        //매칭은 어차피 vi만 찾아서 반영하면 됨.
        List<Matching> matchingList = matchingRepository.findAllByEventIdAndViId(e.getId(), vi.getPrivateId());

        for(Matching m : matchingList){
            User guide = userRepository.findUserByPrivateId(m.getGuideId()).orElse(null);
            if(guide!=null){
                Partner partner = partnerRepository.findByViIdAndGuideId(vi.getPrivateId(),guide.getPrivateId()).orElse(null);
                if(partner !=null){//파트너 정보가 이미 있을 때
                    //log.info("기존 파트너 있음");
                    if(e.getType().equals(EventType.TRAINING) && !partner.getTrainingIds().contains(e.getId())){
                        //log.info("트레이닝 파트너 추가");
                        partner.addTraining(e.getId());
                    }else if(e.getType().equals(EventType.COMPETITION) && !partner.getContestIds().contains(e.getId())){
                        //log.info("대회 파트너 추가");
                        partner.addContest(e.getId());
                    }
                    partnerRepository.save(partner);

                }else{//파트너 정보가 없을 때
                    List<Long> contestIds = new ArrayList<>();
                    List<Long> trainingIds = new ArrayList<>();

                    if(e.getType().equals(EventType.COMPETITION)){
                        //log.info("대회 파트너 추가");
                        contestIds.add(e.getId());
                    }else if(e.getType().equals(EventType.TRAINING)){
                        //log.info("트레이닝 파트너 추가");
                        trainingIds.add(e.getId());
                    }
                    partnerRepository.save(
                            Partner.builder()
                                    .viId(vi.getPrivateId())
                                    .guideId(guide.getPrivateId())
                                    .contestIds(contestIds)
                                    .trainingIds(trainingIds)
                                    .build()
                    );
                }
            }

        }


    }

    @Transactional
    public void setNotAttendPartnerList(long eventId, User vi){
        log.info("setNotAttendPartnerList - privateId : {}", vi.getPrivateId());
        Event e = eventRepository.findById(eventId).orElseThrow(NotExistUserException::new);

        //매칭은 어차피 vi만 찾아서 반영하면 됨.
        List<Matching> matchingList = matchingRepository.findAllByEventIdAndViId(e.getId(), vi.getPrivateId());
        for(Matching m : matchingList) {
            User guide = userRepository.findUserByPrivateId(m.getGuideId()).orElse(null);
            if (guide != null) {
                Partner partner = partnerRepository.findByViIdAndGuideId(vi.getPrivateId(), guide.getPrivateId()).orElse(null);
                if (partner != null) {
                    //log.info("기존 파트너 있음");
                    if (e.getType().equals(EventType.TRAINING) && partner.getTrainingIds().contains(e.getId())) {
                        //log.info("트레이닝 파트너 삭제");
                        partner.removeTraining(e.getId());
                    } else if (e.getType().equals(EventType.COMPETITION) && partner.getContestIds().contains(e.getId())) {
                        //log.info("대회 파트너 삭제");
                        partner.removeContest(e.getId());
                    }
                    partnerRepository.save(partner);

                }
            }
        }

    }

}
