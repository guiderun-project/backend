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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
        maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event/test")
public class EventTestController {
    private final EventRepository eventRepository;
    private final JwtProvider jwtProvider;
    private final EventFormRepository eventFormRepository;
    //kakao3232984128
    @PostMapping
    public void openeventdup(HttpServletRequest request){
        eventRepository.save(Event.builder()
                .id(1L)
                .name("테스트")
                .content("중입니다.")
                        .endTime(LocalDateTime.now())
                .build());
        eventRepository.save(Event.builder()
                .id(2L)
                .name("토스트")
                .content("아닙니다.")
                .endTime(LocalDateTime.now())
                .build());
        eventRepository.save(Event.builder()
                .id(3L)
                .name("ㅇㅇ")
                .content("스트라이크")
                .endTime(LocalDateTime.now())
                .build());
    }

    @GetMapping
    public void closeeventdup(HttpServletRequest request){
        eventRepository.deleteAll();
    }
}
