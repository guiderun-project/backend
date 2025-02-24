package com.guide.run.temp.member.controller;

import com.guide.run.attendance.entity.Attendance;
import com.guide.run.attendance.repository.AttendanceRepository;
import com.guide.run.attendance.service.AttendService;
import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.repository.MatchingRepository;
import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.repository.PartnerRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private final AttendService attendService;

    @GetMapping("/schedule/all")
    public ResponseEntity<String> addSchedule() {
        eventRepository.findAll().forEach(this::processEvent);
        userRepository.findAll().forEach(user -> attendService.countAttendEvent(user.getPrivateId()));
        return ResponseEntity.ok("스케줄 추가 완료");
    }

    @Transactional
    public void processEvent(Event event) {
        if (event == null || event.getId() == null) {
            log.warn("유효하지 않은 이벤트가 전달됨. event: {}", event);
            return;
        }
        log.info("이벤트 반영 시작 - eventId: {}", event.getId());

        attendService.countAttendUser(event.getId(), true);
        List<Attendance> attendances = attendanceRepository.findAllByEventId(event.getId());
        if (attendances == null || attendances.isEmpty()) {
            log.info("해당 이벤트에 대한 출석 정보가 없습니다 - eventId: {}", event.getId());
            return;
        }

        for (Attendance attendance : attendances) {
            log.info("출석 확인 시작 - eventId: {}", event.getId());
            User user = userRepository.findById(attendance.getPrivateId()).orElse(null);

            // 사용자 존재 여부 및 출석 여부 체크
            if (user == null) {
                log.warn("사용자 정보 없음 - privateId: {}", attendance.getPrivateId());
                continue;
            }
            if (!attendance.isAttend()) {
                log.info("출석하지 않은 사용자 - privateId: {}", attendance.getPrivateId());
                continue;
            }
            if (!UserType.VI.equals(user.getType())) {
                log.info("해당 사용자의 타입이 VI가 아님 - privateId: {}", attendance.getPrivateId());
                continue;
            }

            List<Matching> matchings = matchingRepository.findAllByEventIdAndViId(attendance.getEventId(), user.getPrivateId());
            if (matchings == null || matchings.isEmpty()) {
                log.info("해당 사용자에 대한 매칭 정보가 없음 - eventId: {}, viId: {}", attendance.getEventId(), user.getPrivateId());
                continue;
            }

            for (Matching matching : matchings) {
                log.info("매칭 반영 시작 - eventId: {}", event.getId());

                // 가이드의 출석 정보 null 체크
                Attendance guideAttendance = attendanceRepository.findByEventIdAndPrivateId(event.getId(), matching.getGuideId());
                if (guideAttendance == null) {
                    log.warn("가이드의 출석 정보가 없음 - guideId: {} / eventId: {}", matching.getGuideId(), event.getId());
                    continue;
                }
                if (!guideAttendance.isAttend()) {
                    log.info("해당 가이드는 출석하지 않음 - guideId: {} / eventId: {}", matching.getGuideId(), event.getId());
                    continue;
                }

                // 파트너 정보 조회(없으면 신규 생성)
                Partner partner = partnerRepository.findByViIdAndGuideId(matching.getViId(), matching.getGuideId())
                        .orElseGet(() -> Partner.builder()
                                .viId(matching.getViId())
                                .guideId(matching.getGuideId())
                                .contestIds(new HashSet<>())
                                .trainingIds(new HashSet<>())
                                .build());

                boolean isTraining = EventType.TRAINING.equals(event.getType());
                addEventToPartner(partner, matching.getEventId(), isTraining);
            }
        }
        log.info("이벤트 반영 완료 - eventId: {}", event.getId());
    }

    /**
     * 파트너 엔티티에 이벤트 ID를 추가합니다.
     * (이미 존재하면 추가하지 않습니다.)
     */
    @Transactional
    public void addEventToPartner(Partner partner, Long eventId, boolean isTraining) {
        if (partner == null) {
            log.warn("파트너 정보가 null입니다. eventId: {}", eventId);
            return;
        }
        Set<Long> eventIds = isTraining ? partner.getTrainingIds() : partner.getContestIds();
        if (eventIds == null) {
            eventIds = new HashSet<>();
        }
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
