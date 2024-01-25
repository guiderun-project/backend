package com.guide.run.event.entity.controller;

import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.request.EventUpdateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.entity.service.EventService;
import com.guide.run.global.exception.admin.authorize.NotAuthorityAdminException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.entity.User;
import com.guide.run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.guide.run.user.entity.type.Role.VADMIN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {
    private final JwtProvider jwtProvider;
    private final EventService eventService;
    private final UserRepository userRepository;
    @PostMapping
    public ResponseEntity<EventCreatedResponse> eventCreate(@RequestBody EventCreateRequest eventCreateRequest, HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        User user = userRepository.findByUserId(userId).
                orElseThrow(() -> new NotExistUserException());
        if(user.getRole().getValue() != VADMIN.getValue())
            throw new NotAuthorityAdminException();
        EventCreatedResponse eventCreatedResponse = eventService.eventCreate(eventCreateRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventCreatedResponse);
    }
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventUpdatedResponse> eventUpdate(@PathVariable Long eventId,@RequestBody EventUpdateRequest eventUpdateRequest, HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        User user = userRepository.findByUserId(userId).
                orElseThrow(() -> new NotExistUserException());
        if(user.getRole().getValue() != VADMIN.getValue())
            throw new NotAuthorityAdminException();
        EventUpdatedResponse eventUpdatedResponse = eventService.eventUpdate(eventUpdateRequest, userId,eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventUpdatedResponse);
    }
}
