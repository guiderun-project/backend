package com.guide.run.event.controller;

import com.guide.run.event.entity.dto.request.form.CreateEventForm;
import com.guide.run.event.entity.dto.response.form.CreatedForm;
import com.guide.run.event.entity.dto.response.form.GetAllForms;
import com.guide.run.event.entity.dto.response.form.GetForm;
import com.guide.run.event.service.EventFormService;
import com.guide.run.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventFormController {
    private final EventFormService eventFormService;
    private final JwtProvider jwtProvider;
    @PostMapping("/{eventId}/form")
    public ResponseEntity<CreatedForm> createForm(@RequestBody CreateEventForm createForm,
                                                  @PathVariable("eventId") Long eventId,
                                                  HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(CreatedForm.builder()
                .requestId(eventFormService.createForm(createForm,eventId,userId))
                .build());
    }

    @PatchMapping("/{eventId}/form")
    public ResponseEntity<CreatedForm> patchForm(@RequestBody CreateEventForm createForm,
                                                  @PathVariable("eventId") Long eventId,
                                                  HttpServletRequest request){
        String userId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(CreatedForm.builder()
                .requestId(eventFormService.patchForm(createForm,eventId,userId))
                .build());
    }
    @GetMapping("/{eventId}/form/{userId}")
    public ResponseEntity<GetForm> getForm(@PathVariable("eventId") Long eventId,
                                           @PathVariable("userId") String userId,
                                           HttpServletRequest request){
        return ResponseEntity.ok().body(eventFormService.getForm(eventId,userId));
    }
    @GetMapping("/{eventId}/forms/all")
    public ResponseEntity<GetAllForms> getAllForms(@PathVariable("eventId") Long eventId, HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok().body(eventFormService.getAllForms(eventId,privateId));
    }
    @DeleteMapping("/{eventId}/form")
    public ResponseEntity<String> deleteForm(@PathVariable("eventId") Long eventId,
                                             HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        eventFormService.deleteForm(eventId,privateId);
        return ResponseEntity.ok("200 ok");
    }
}
