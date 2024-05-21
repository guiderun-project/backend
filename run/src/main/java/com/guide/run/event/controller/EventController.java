package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.request.EventUpdateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.service.EventService;
import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {
    private final JwtProvider jwtProvider;
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventCreatedResponse> eventCreate(@RequestBody EventCreateRequest eventCreateRequest, HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        EventCreatedResponse eventCreatedResponse = eventService.eventCreate(eventCreateRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventCreatedResponse);
    }
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventUpdatedResponse> eventUpdate(@PathVariable Long eventId,@RequestBody EventUpdateRequest eventUpdateRequest, HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        EventUpdatedResponse eventUpdatedResponse = eventService.eventUpdate(eventUpdateRequest, userId,eventId);
        return ResponseEntity.status(HttpStatus.OK).body(eventUpdatedResponse);
    }

    @PatchMapping("/close/{eventId}")
    public ResponseEntity<String> eventUpdate(@PathVariable Long eventId, HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        eventService.eventClose(userId, eventId);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> eventDelete(@PathVariable Long eventId,HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);

        eventService.eventDelete(userId,eventId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/pop/{eventId}")
    public ResponseEntity<EventPopUpResponse> eventPopUp(@PathVariable Long eventId, HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        EventPopUpResponse response = eventService.eventPopUp(eventId, userId);
        return ResponseEntity.ok().body(response);
    }
}
