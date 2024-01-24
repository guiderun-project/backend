package com.guide.run.event.entity.controller;

import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.service.EventService;
import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {
    private final JwtProvider jwtProvider;
    private final EventService eventService;
    @PostMapping("/")
    public ResponseEntity<EventCreatedResponse> eventCreate(@RequestBody EventCreateRequest eventCreateRequest, HttpServletRequest request){
        String userId = jwtProvider.resolveToken(request);
        EventCreatedResponse eventCreatedResponse = eventService.eventCreate(eventCreateRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventCreatedResponse);
    }
}
