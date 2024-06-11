package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.response.EventPopUpResponse;
import com.guide.run.event.entity.dto.request.EventCreateRequest;
import com.guide.run.event.entity.dto.response.EventCreatedResponse;
import com.guide.run.event.entity.dto.response.EventUpdatedResponse;
import com.guide.run.event.entity.dto.response.get.DetailEvent;
import com.guide.run.event.entity.dto.response.get.MyEventDdayResponse;
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
    public ResponseEntity<EventCreatedResponse> eventCreate(@RequestBody EventCreateRequest request, HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        EventCreatedResponse eventCreatedResponse = eventService.eventCreate(request, privateId);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventCreatedResponse);
    }
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventUpdatedResponse> eventUpdate(@PathVariable Long eventId,@RequestBody EventCreateRequest request, HttpServletRequest httpServletRequest){
        String priavateId = jwtProvider.extractUserId(httpServletRequest);
        EventUpdatedResponse eventUpdatedResponse = eventService.eventUpdate(request, priavateId,eventId);
        return ResponseEntity.status(HttpStatus.OK).body(eventUpdatedResponse);
    }

    @PatchMapping("/close/{eventId}")
    public ResponseEntity<String> eventClose(@PathVariable Long eventId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        eventService.eventClose(privateId, eventId);
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
        String privateId = jwtProvider.extractUserId(request);
        EventPopUpResponse response = eventService.eventPopUp(eventId, privateId);
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/dday")
    public ResponseEntity<MyEventDdayResponse> getMyEventDday(HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().
                body(eventService.getMyEventDday(userId));
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<DetailEvent> getDetailEvent(@PathVariable("eventId")Long eventId,
                                                      HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().
                body(eventService.getDetailEvent(eventId,userId));

    }
}
