package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.request.EventUpdateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.service.EventService;
import com.guide.run.global.exception.admin.authorize.NotAuthorityAdminException;
import com.guide.run.global.exception.user.resource.NotExistUserException;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.user.entity.user.User;
import com.guide.run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"https://guide-run-qa.netlify.app", "https://guiderun.org",
        "https://guide-run.netlify.app","https://www.guiderun.org", "http://localhost:3000"},
        maxAge = 3600)
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
        User user = userRepository.findUserByPrivateId(userId).
                orElseThrow(() -> new NotExistUserException());

        EventCreatedResponse eventCreatedResponse = eventService.eventCreate(eventCreateRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventCreatedResponse);
    }
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventUpdatedResponse> eventUpdate(@PathVariable Long eventId,@RequestBody EventUpdateRequest eventUpdateRequest, HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        User user = userRepository.findUserByPrivateId(userId).
                orElseThrow(() -> new NotExistUserException());

        EventUpdatedResponse eventUpdatedResponse = eventService.eventUpdate(eventUpdateRequest, userId,eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventUpdatedResponse);
    }
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> eventDelete(@PathVariable Long eventId,HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        User user = userRepository.findUserByPrivateId(userId).
                orElseThrow(() -> new NotExistUserException());

        eventService.eventDelete(userId,eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/pop/{eventId}")
    public ResponseEntity<EventPopUpResponse> eventPopUp(@PathVariable Long eventId, HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        User user = userRepository.findUserByPrivateId(userId).
                orElseThrow(() -> new NotExistUserException());
        EventPopUpResponse response = eventService.eventPopUp(eventId);

        return ResponseEntity.ok().body(response);
    }
}
