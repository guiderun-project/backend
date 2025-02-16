package com.guide.run.temp.member.controller;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
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
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController("/tmp")
@RequiredArgsConstructor
@Log4j2
public class TMPController {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final MatchingRepository matchingRepository;
    private final PartnerRepository partnerRepository;

    @GetMapping("/schedule/all")
    public ResponseEntity<String> addSchedule() {
        eventRepository.findAll().forEach(this::processEvent);
        eventRepository.findAll().forEach(this::setEventResult);
        return ResponseEntity.ok("스케줄 추가 완료");
    }

    @Transactional
    public void processEvent(Event event) {
            //이벤트 출석 내역 불러옴
            List<Attendance> attendances = attendanceRepository.findAllByEventId(event.getId());

            for (Attendance attendance : attendances) {
                User user = userRepository.findById(attendance.getPrivateId()).orElse(null);
                if (user != null && attendance.isAttend()) { //출석했고 유저 있음
                    if (user.getType().equals(UserType.VI)) {
                        //이벤트 매칭 내역도 불러옴
                        List<Matching> matchings = matchingRepository.findAllByEventIdAndViId(attendance.getEventId(), user.getPrivateId());
                        //히스토리 추가. 근데 이미 있는 이벤트면 추가 안함.
                        for (Matching matching : matchings) {
                            Partner partner = partnerRepository.findByViIdAndGuideId(matching.getViId(), matching.getGuideId()).orElse(null);
                            if (partner != null) {
                                if (!partner.getTrainingIds().contains(matching.getEventId()) && !partner.getContestIds().contains(matching.getEventId())) {
                                    if (event.getType().equals(EventType.TRAINING)) {
                                        partner.addTraining(matching.getEventId());
                                        partnerRepository.save(partner);
                                    } else {
                                        partner.addContest(matching.getEventId());
                                        partnerRepository.save(partner);
                                    }
                                }
                            } else {
                                partner = Partner.builder()
                                        .viId(matching.getViId())
                                        .guideId(matching.getGuideId())
                                        .contestIds(new ArrayList<>())
                                        .trainingIds(new ArrayList<>())
                                        .build();
                                if (event.getType().equals(EventType.TRAINING)) {
                                    partner.addTraining(matching.getEventId());
                                    partnerRepository.save(partner);
                                } else {
                                    partner.addContest(matching.getEventId());
                                    partnerRepository.save(partner);
                                }
                            }
                        }
                    }


                }
            }
    }


    @Transactional
    public void addEventToPartner(Partner partner, Long eventId, boolean isTraining) {
        // 해당 이벤트가 이미 기록되어 있는지 체크 후 없으면 추가
        List<Long> eventIds = isTraining ? partner.getTrainingIds() : partner.getContestIds();
        if (!eventIds.contains(eventId)) {
            if (isTraining) {
                partner.addTraining(eventId);
            } else {
                partner.addContest(eventId);
            }
            partnerRepository.save(partner);
        }
    }

    //이벤트 파트너, 참여인원 반영
    @Transactional
    public void setEventResult(Event e){
        log.info("setEventResult");
        int viCnt = 0;
        int guideCnt = 0;

        //출석 리스트
        List<Attendance> attendances = attendanceRepository.getAttendanceTrue(e.getId(), true);

        for(Attendance a : attendances){

            //출석한 유저 찾기
            User user = userRepository.findById(a.getPrivateId()).orElse(null);

            if(user!=null) {
                if (user.getType().equals(UserType.VI)) {
                    //참여 vi 수 증가
                    viCnt += 1;

                } else if (user.getType().equals(UserType.GUIDE)) {
                    guideCnt += 1;
                }

                //참여 이벤트 개수 반영
                if (e.getType().equals(EventType.TRAINING)) {
                    user.addTrainingCnt(1);
                } else if (e.getType().equals(EventType.COMPETITION)) {
                    user.addContestCnt(1);
                }

                userRepository.save(user);
            }
        }

        //참여 인원 반영
        e.setCnt(viCnt, guideCnt);
        eventRepository.save(e);
    }


}




