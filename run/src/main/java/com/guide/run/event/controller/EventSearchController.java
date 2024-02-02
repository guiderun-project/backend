package com.guide.run.event.controller;


import com.guide.run.event.entity.dto.MyEvent;
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
    public ResponseEntity<List<MyEvent>> getMyOpenEventList(@RequestParam("sort") String sort
    , @RequestParam("year") int year, @RequestParam("month") int month, HttpServletRequest request)
    {
        String privateId = jwtProvider.extractUserId(request);
        User user = userRepository.findByUserId(privateId).
                orElseThrow(() -> new NotExistUserException());
        List<MyEvent> myEvent = eventSearchService.getMyEvent(sort, year, month,privateId);
        return ResponseEntity.status(200).body(myEvent);
    }
}
