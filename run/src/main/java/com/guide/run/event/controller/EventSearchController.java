package com.guide.run.event.controller;


import com.guide.run.event.entity.dto.response.search.MyEventResponse;
import com.guide.run.event.entity.dto.response.search.UpcomingEventResponse;
import com.guide.run.event.entity.repository.EventRepository;
import com.guide.run.event.entity.service.EventSearchService;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.entity.User;
import com.guide.run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventSearchController {
    private final JwtProvider jwtProvider;
    private final EventSearchService eventSearchService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @GetMapping("/my")
    public ResponseEntity<List<MyEventResponse>> getMyEventList(@RequestParam("sort") String sort
    , @RequestParam("year") int year, @RequestParam("month") int month, HttpServletRequest request)
    {
        String privateId = jwtProvider.extractUserId(request);
        userRepository.findByUserId(privateId).
                orElseThrow(() -> new NotExistUserException());
        List<MyEventResponse> myEvents = eventSearchService.getMyEvent(sort, year, month,privateId);
        return ResponseEntity.status(200).body(myEvents);
    }
    @GetMapping("/upcoming")
    public ResponseEntity<List<UpcomingEventResponse>> getUpcomingEventList(@RequestParam("sort") String sort,
                                                                            HttpServletRequest request)
    {
        String privateId = jwtProvider.extractUserId(request);
        userRepository.findByUserId(privateId).
                orElseThrow(() -> new NotExistUserException());
        List<UpcomingEventResponse> upcomingEvents = eventSearchService.getUpcomingEvent(sort, privateId);
        return ResponseEntity.status(200).body(upcomingEvents);
    }
}
