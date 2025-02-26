package com.guide.run.event.controller;

import com.guide.run.event.entity.*;
import com.guide.run.event.entity.repository.*;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.global.exception.event.resource.NotExistCommentException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.guide.run.event.entity.type.EventType.COMPETITION;
import static com.guide.run.event.entity.type.EventType.TRAINING;

@Slf4j
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
    private final EventLikeRepository eventLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final EventCommentRepository eventCommentRepository;
    private final UserRepository userRepository;
    //kakao3232984128
    @PostMapping
    public void openeventdup(HttpServletRequest request){
        eventRepository.save(Event.builder()
                .id(1L)
                .name("테스트")
                .content("중입니다.")
                        .type(COMPETITION)
                        .endTime(LocalDateTime.now().plusDays(2))
                        .startTime(LocalDateTime.now().plusDays(2))
                        .recruitStatus(EventRecruitStatus.RECRUIT_UPCOMING)
                .build());
        eventFormRepository.save(EventForm.builder()
                        .id(1L)
                        .eventId(1L)
                .privateId("kakao3232984128")
                        .build());

        userRepository.save(
                User.builder()
                        .privateId("22")
                        .userId("dd")
                        .build()
        );

        eventCommentRepository.save(
                Comment.builder()
                        .eventId(1L)
                        .commentId(1L)
                        .privateId("22")
                        .comment("두번째사람")
                        .build()
        );


        eventRepository.save(Event.builder()
                .id(2L)
                .name("토스트")
                .type(COMPETITION)
                .content("아닙니다.")
                .endTime(LocalDateTime.now().plusDays(3))
                .startTime(LocalDateTime.now().plusDays(3))
                .recruitStatus(EventRecruitStatus.RECRUIT_END)
                .build());
        eventFormRepository.save(EventForm.builder()
                        .id(2L)
                        .eventId(2L)
                .privateId("kakao32329841282")
                .build());
        eventRepository.save(Event.builder()
                .id(3L)
                .type(COMPETITION)
                .name("ㅇㅇ")
                .content("스트라이크")
                .endTime(LocalDateTime.now().plusDays(4))
                .startTime(LocalDateTime.now().plusDays(4))
                .recruitStatus(EventRecruitStatus.RECRUIT_OPEN)
                .build());
        eventFormRepository.save(EventForm.builder()
                .id(3L)
                        .eventId(3L)
                .privateId("kakao3232984128")
                .build());
        eventRepository.save(Event.builder()
                .id(4L)
                .type(COMPETITION)
                .name("네번쨰")
                .content("네번째입니다")
                .endTime(LocalDateTime.now().minusDays(6))
                .startTime(LocalDateTime.now().minusDays(6))
                .recruitStatus(EventRecruitStatus.RECRUIT_END)
                .build());
        eventFormRepository.save(EventForm.builder()
                .id(4L)
                .eventId(4L)
                .privateId("kakao3232984128")
                .build());
        eventRepository.save(Event.builder()
                .id(5L)
                .name("테스트3")
                .type(TRAINING)
                .content("중입니다.")
                .endTime(LocalDateTime.now().plusDays(8))
                .startTime(LocalDateTime.now().plusDays(8))
                .recruitStatus(EventRecruitStatus.RECRUIT_UPCOMING)
                .build());
        eventFormRepository.save(EventForm.builder()
                .id(5L)
                .eventId(5L)
                .privateId("kakao3232984128")
                .build());
        eventRepository.save(Event.builder()
                .id(6L)
                .name("테스트4")
                .type(TRAINING)
                .content("중입니다.")
                .endTime(LocalDateTime.now().plusDays(7))
                .startTime(LocalDateTime.now().plusDays(7))
                .recruitStatus(EventRecruitStatus.RECRUIT_CLOSE)
                .build());
        eventFormRepository.save(EventForm.builder()
                .id(6L)
                .eventId(6L)
                .privateId("kakao3232984128")
                .build());
        eventRepository.save(Event.builder()
                .id(7L)
                .name("테스트4")
                .content("중입니다.")
                .type(TRAINING)
                .endTime(LocalDateTime.now().plusDays(4))
                .startTime(LocalDateTime.now().plusDays(4))
                .recruitStatus(EventRecruitStatus.RECRUIT_OPEN)
                .build());
        eventFormRepository.save(EventForm.builder()
                .id(7L)
                .eventId(7L)
                .privateId("kakao3232984128")
                .build());
        eventRepository.save(Event.builder()
                .id(8L)
                .name("다른")
                .type(TRAINING)
                .content("계정")
                .endTime(LocalDateTime.now().plusDays(2))
                .startTime(LocalDateTime.now().plusDays(2))
                .recruitStatus(EventRecruitStatus.RECRUIT_CLOSE)
                .build());
        eventFormRepository.save(EventForm.builder()
                .id(8L)
                .eventId(8L)
                .privateId("kakao32329841282")
                .build());
    }

    @GetMapping
    public void closeeventdup(HttpServletRequest request){
        log.error("테스트");

    }
}
