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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/tmp")
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
            return ResponseEntity.ok("스케줄 추가 완료");
        }

        @Transactional
        public void processEvent(Event event) {
            log.info("이벤트 반영 시작 - eventId: {}", event.getId());
            List<Attendance> attendances = attendanceRepository.findAllByEventId(event.getId());

            for (Attendance attendance : attendances) {
                log.info("출석 확인 시작 - eventId: {}", event.getId());
                User user = userRepository.findById(attendance.getPrivateId()).orElse(null);

                // 출석 여부와 사용자 존재 여부 확인
                if (user == null || !attendance.isAttend()) {
                    continue;
                }

                // NPE 방지를 위해 안전하게 비교
                if (!UserType.VI.equals(user.getType())) {
                    continue;
                }

                // 해당 사용자의 매칭 내역 불러오기
                List<Matching> matchings = matchingRepository.findAllByEventIdAndViId(attendance.getEventId(), user.getPrivateId());
                for (Matching matching : matchings) {
                    log.info("매칭 반영 시작 - eventId: {}", event.getId());

                    // 파트너 조회. 존재하지 않으면 신규 생성
                    Partner partner = partnerRepository.findByViIdAndGuideId(matching.getViId(), matching.getGuideId())
                            .orElseGet(() -> Partner.builder()
                                    .viId(matching.getViId())
                                    .guideId(matching.getGuideId())
                                    .contestIds(new ArrayList<>())
                                    .trainingIds(new ArrayList<>())
                                    .build());

                    boolean isTraining = EventType.TRAINING.equals(event.getType());
                    addEventToPartner(partner, matching.getEventId(), isTraining);
                }
            }
            log.info("이벤트 반영 완료 - eventId: {}", event.getId());
        }

        /**
         * 파트너 엔티티에 이벤트 id를 추가하는 메서드
         * (이미 추가되어 있다면 추가하지 않습니다.)
         */
        @Transactional
        public void addEventToPartner(Partner partner, Long eventId, boolean isTraining) {
            List<Long> eventIds = isTraining ? partner.getTrainingIds() : partner.getContestIds();
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
        }
    }





