package com.guide.run.event.controller;

import com.guide.run.event.entity.Event;
import com.guide.run.event.entity.EventForm;
import com.guide.run.event.entity.repository.EventFormRepository;
import com.guide.run.event.entity.repository.EventRepository;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event/test")
public class EventTestController {
    private final EventRepository eventRepository;
    private final JwtProvider jwtProvider;
    private final EventFormRepository eventFormRepository;

    @PostMapping
    public void eventdup(HttpServletRequest request){
        eventRepository.save(Event.builder()
                .organizer(jwtProvider.extractUserId(request))
                .recruitStartDate(LocalDate.now())
                .recruitEndDate(LocalDate.now())
                .name("3")
                .recruitStatus(EventRecruitStatus.OPEN)
                .isApprove(true)
                .type(EventType.Training)
                .startTime(LocalDateTime.now().minusDays(7))
                .endTime(LocalDateTime.now().minusDays(7))
                .maxNumV(20)
                .maxNumG(20)
                .place("3")
                .content("3")
                .build());
        eventFormRepository.save(EventForm.builder()
                .privateId("kakao3232984128")
                .eventId(1L)
                        .isMatching(true)
                .startTime(LocalDateTime.now().minusDays(7))
                .endTime(LocalDateTime.now().minusDays(7))
                .build());
        eventRepository.save(Event.builder()
                .organizer(jwtProvider.extractUserId(request))
                .recruitStartDate(LocalDate.now())
                .recruitEndDate(LocalDate.now())
                .name("dd")
                .recruitStatus(EventRecruitStatus.OPEN)
                .isApprove(true)
                .type(EventType.Training)
                .startTime(LocalDateTime.now().minusDays(10))
                .endTime(LocalDateTime.now().minusDays(10))
                .maxNumV(30)
                .maxNumG(30)
                .place("5")
                .content("5")
                .build());
        eventFormRepository.save(EventForm.builder()
                .privateId("kakao3232984128")
                .eventId(2L)
                .isMatching(true)
                .startTime(LocalDateTime.now().minusDays(10))
                .endTime(LocalDateTime.now().minusDays(10))
                .build());
        eventRepository.save(Event.builder()
                .organizer(jwtProvider.extractUserId(request))
                .recruitStartDate(LocalDate.now())
                .recruitEndDate(LocalDate.now())
                .name("dde2")
                .recruitStatus(EventRecruitStatus.OPEN)
                .isApprove(true)
                .type(EventType.Training)
                .startTime(LocalDateTime.now().minusDays(4))
                .endTime(LocalDateTime.now().minusDays(4))
                .maxNumV(10)
                .maxNumG(10)
                .place("6")
                .content("6")
                .build());
        eventRepository.save(Event.builder()
                .organizer(jwtProvider.extractUserId(request))
                .recruitStartDate(LocalDate.now())
                .recruitEndDate(LocalDate.now())
                .name("dde2")
                .recruitStatus(EventRecruitStatus.OPEN)
                .isApprove(true)
                .type(EventType.Training)
                .startTime(LocalDateTime.now().plusDays(4))
                .endTime(LocalDateTime.now().plusDays(4))
                .maxNumV(106)
                .maxNumG(108)
                .place("67")
                .content("68")
                .build());
        eventFormRepository.save(EventForm.builder()
                .privateId("kakao3232984128")
                .eventId(4L)
                .isMatching(true)
                .startTime(LocalDateTime.now().plusDays(4))
                .endTime(LocalDateTime.now().plusDays(4))
                .build());
        eventRepository.save(Event.builder()
                .organizer(jwtProvider.extractUserId(request))
                .recruitStartDate(LocalDate.now())
                .recruitEndDate(LocalDate.now())
                .name("dde2")
                .recruitStatus(EventRecruitStatus.OPEN)
                .isApprove(true)
                .type(EventType.Training)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusDays(1))
                .maxNumV(106)
                .maxNumG(108)
                .place("67")
                .content("68")
                .build());
        eventFormRepository.save(EventForm.builder()
                .privateId("kakao3232984128")
                .eventId(5L)
                .isMatching(true)
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().minusDays(1))
                .build());
        eventRepository.save(Event.builder()
                .organizer(jwtProvider.extractUserId(request))
                .recruitStartDate(LocalDate.now())
                .recruitEndDate(LocalDate.now())
                .name("dde2")
                .recruitStatus(EventRecruitStatus.OPEN)
                .isApprove(true)
                .type(EventType.Training)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .maxNumV(106)
                .maxNumG(108)
                .place("67")
                .content("68")
                .build());
        eventFormRepository.save(EventForm.builder()
                .privateId("kakao3232984128")
                .eventId(6L)
                .isMatching(true)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now())
                .build());
    }

}
