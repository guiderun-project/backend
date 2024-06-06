package com.guide.run.user.controller;

import com.guide.run.admin.dto.EventTypeCountDto;
import com.guide.run.event.entity.dto.response.get.Count;
import com.guide.run.event.entity.dto.response.get.MyPageEvent;
import com.guide.run.global.jwt.JwtProvider;
import com.guide.run.partner.entity.dto.MyPagePartner;
import com.guide.run.user.dto.GlobalUserInfoDto;
import com.guide.run.user.dto.response.MyPageEventList;
import com.guide.run.user.dto.response.ProfileResponse;
import com.guide.run.user.service.MypageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class MypageController {

    private final JwtProvider jwtProvider;
    private final MypageService mypageService;

    @GetMapping("/personal")
    public ResponseEntity<GlobalUserInfoDto> getGlobalUserInfo(HttpServletRequest httpServletRequest){
        String privateId = jwtProvider.extractUserId(httpServletRequest);
        GlobalUserInfoDto response = mypageService.getGlobalUserInfo(privateId);

        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/event-history/count/{userId}")
    public ResponseEntity<Count> getMyEventCount(@PathVariable String userId,
                                                 @RequestParam(defaultValue = "RECRUIT_ALL") String kind,
                                                 @RequestParam(defaultValue = "0") int year){
        if(year==0){
            year = Integer.parseInt(String.valueOf(LocalDate.now().getYear()));
        }
        Count response = Count.builder()
                .count(mypageService.getMyPageEventsCount(userId, kind, year))
                .build();
        return ResponseEntity.ok().body(response);
    }
    @GetMapping("/event-history/{userId}")
    public ResponseEntity<MyPageEventList> getMyEventList(@PathVariable String userId,
                                                          @RequestParam(defaultValue = "0") int start,
                                                          @RequestParam(defaultValue = "10") int limit,
                                                          @RequestParam(defaultValue = "RECRUIT_ALL") String kind,
                                                          @RequestParam(defaultValue = "0") int year){
       if(year==0){
           year = Integer.parseInt(String.valueOf(LocalDate.now().getYear()));
       }
        MyPageEventList response = MyPageEventList.builder()
                .items(mypageService.getMyPageEvents(userId,start,limit, kind, year))
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable String userId,
                                                          HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        ProfileResponse response = mypageService.getUserProfile(userId,privateId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/partner-list/{userId}")
    public ResponseEntity<List<MyPagePartner>> getMyPartnerList(@PathVariable String userId,
                                                                @RequestParam(defaultValue = "0") int start,
                                                                @RequestParam(defaultValue = "10") int limit,
                                                                @RequestParam(defaultValue = "RECENT") String sort){
        List<MyPagePartner> response = mypageService.getMyPagePartners(userId, start, limit, sort);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/partner-list/count/{userId}")
    public ResponseEntity<Count> getMypartnerCount(@PathVariable String userId){
        Count response = Count.builder()
                .count(mypageService.getMyPartnersCount(userId))
                .build();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("event-type/count")
    public ResponseEntity<EventTypeCountDto> getEventTypeCount(HttpServletRequest request){
        String privateId = jwtProvider.extractUserId(request);
        return ResponseEntity.ok(mypageService.getMyPageEventTypeCount(privateId));
    }
}
