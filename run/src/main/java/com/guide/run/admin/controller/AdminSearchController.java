package com.guide.run.admin.controller;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.admin.dto.condition.AllSortCond;
import com.guide.run.admin.dto.condition.EventSortCond;
import com.guide.run.admin.dto.condition.UserSortCond;
import com.guide.run.admin.dto.condition.WithdrawalSortCond;
import com.guide.run.admin.dto.response.AdminSearchList;
import com.guide.run.admin.dto.response.event.AdminEventHistoryList;
import com.guide.run.admin.dto.response.event.AdminEventList;
import com.guide.run.admin.dto.response.partner.AdminPartnerList;
import com.guide.run.admin.dto.response.user.AdminUserList;
import com.guide.run.admin.dto.response.user.UserItem;
import com.guide.run.admin.dto.response.user.WithdrawalList;
import com.guide.run.admin.service.AdminEventService;
import com.guide.run.admin.service.AdminPartnerService;
import com.guide.run.admin.service.AdminUserService;
import com.guide.run.admin.service.AdminWithdrawalService;
import com.guide.run.event.entity.dto.response.get.Count;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/search")
public class AdminSearchController {
    private final AdminUserService adminUserService;
    private final AdminEventService adminEventService;
    private final AdminPartnerService adminPartnerService;
    private final AdminWithdrawalService adminWithdrawalService;

    //전체 검색
    @GetMapping("/all")
    public ResponseEntity<AdminSearchList> searchAll(@RequestParam(defaultValue = "") String text,
                                                     @RequestParam(defaultValue = "0") int start,
                                                     @RequestParam(defaultValue = "10") int limit,

                                                     @RequestParam(defaultValue = "2") int user_time,
                                                     @RequestParam(defaultValue = "2") int type,
                                                     @RequestParam(defaultValue = "2") int gender,
                                                     @RequestParam(defaultValue = "2") int name_team,
                                                     @RequestParam(defaultValue = "2") int user_approval,

                                                     @RequestParam(defaultValue = "2") int event_time,
                                                     @RequestParam(defaultValue = "2") int name,
                                                     @RequestParam(defaultValue = "2") int organizer,
                                                     @RequestParam(defaultValue = "2") int event_approval
                                      ){
        UserSortCond userSortCond = UserSortCond.builder()
                .type(user_time)
                .approval(user_approval)
                .name_team(name_team)
                .gender(gender)
                .type(type)
                .build();

        EventSortCond eventSortCond = EventSortCond.builder()
                .approval(event_approval)
                .name(name)
                .organizer(organizer)
                .time(event_time)
                .build();

        List<UserItem> userItems = adminUserService.searchUser(start, limit, userSortCond, text);
        List<EventDto> eventItems = adminEventService.searchAllEvent(text, start, limit, eventSortCond);

        AdminSearchList response = AdminSearchList.builder()
                .event_items(eventItems)
                .user_items(userItems)
                .build();

        return ResponseEntity.ok(response);
    }
    //회원 검색
    @GetMapping("/user")
    public ResponseEntity<AdminUserList> searchUser(@RequestParam(defaultValue = "") String text,
                                                    @RequestParam(defaultValue = "0") int start,
                                                    @RequestParam(defaultValue = "10") int limit,

                                                    @RequestParam(defaultValue = "2") int time,
                                                    @RequestParam(defaultValue = "2") int type,
                                                    @RequestParam(defaultValue = "2") int gender,
                                                    @RequestParam(defaultValue = "2") int name_team,
                                                    @RequestParam(defaultValue = "2") int approval){
        UserSortCond cond = UserSortCond.builder()
                .time(time)
                .type(type)
                .gender(gender)
                .name_team(name_team)
                .approval(approval)
                .build();
        AdminUserList response = AdminUserList.builder()
                        .items(adminUserService.searchUser(start, limit, cond, text))
                        .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/count")
    public ResponseEntity<Count> searchUserCount(@RequestParam(defaultValue = "") String text){
        Count response = adminUserService.searchUserCount(text);
        return ResponseEntity.ok(response);
    }

    //이벤트 검색
    @GetMapping("/event")
    public ResponseEntity<AdminEventList> searchEvent(@RequestParam(defaultValue = "") String text,
                                                      @RequestParam(defaultValue = "0") int start,
                                                      @RequestParam(defaultValue = "10") int limit,
                                                      @RequestParam(defaultValue = "2") int time,
                                                      @RequestParam(defaultValue = "2") int name,
                                                      @RequestParam(defaultValue = "2") int organizer,
                                                      @RequestParam(defaultValue = "2") int approval){
        EventSortCond cond = EventSortCond.builder()
                .time(time)
                .name(name)
                .organizer(organizer)
                .approval(approval)
                .build();
        AdminEventList eventList = AdminEventList.builder()
                .items(adminEventService.searchAllEvent(text, start, limit, cond))
                .build();
        return ResponseEntity.ok(eventList);
    }

    @GetMapping("/event/count")
    public ResponseEntity<Count> searchEventCount(@RequestParam(defaultValue = "") String text){
        return ResponseEntity.ok(adminEventService.searchAllEventCount(text));
    }

    //탈퇴한 회원 검색
    @GetMapping("/withdrawal-list")
    public ResponseEntity<WithdrawalList> searchWithDrawal(@RequestParam(defaultValue = "") String text,
                                                           @RequestParam(defaultValue = "0") int start,
                                                           @RequestParam(defaultValue = "10") int limit,
                                                           @RequestParam(defaultValue = "2") int time,
                                                           @RequestParam(defaultValue = "2") int type,
                                                           @RequestParam(defaultValue = "2") int gender,
                                                           @RequestParam(defaultValue = "2") int name_team){
        WithdrawalSortCond cond = WithdrawalSortCond.builder()
                .time(time)
                .type(type)
                .gender(gender)
                .name_team(name_team)
                .build();

        WithdrawalList response = WithdrawalList.builder()
                .items(adminWithdrawalService.searchWithdrawal(text,start,limit,cond))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/withdrawal-list/count")
    public ResponseEntity<Count> searchWithDrawalCount(@RequestParam(defaultValue = "") String text){
        return ResponseEntity.ok(adminWithdrawalService.searchWithdrawalCount(text));
    }

    //파트너 검색
    @GetMapping("/partner-list/{userId}")
    public ResponseEntity<AdminPartnerList> searchPartner(@PathVariable String userId,
                                                          @RequestParam(defaultValue = "") String text,
                                                          @RequestParam int start,
                                                          @RequestParam int limit){
        AdminPartnerList response = AdminPartnerList.builder()
                .items(adminPartnerService.searchPartnerList(userId, text, start, limit))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/partner-list/count/{userId}")
    public ResponseEntity<Count> searchPartnerCount(@PathVariable String userId,
                                                    @RequestParam(defaultValue = "") String text){
        return ResponseEntity.ok(adminPartnerService.searchPartnerCount(userId, text));
    }

    //이벤트 히스토리 검색
    @GetMapping("/event-list/{userId}")
    public ResponseEntity<AdminEventHistoryList>searchEventHistory(@PathVariable String userId,
                                                                   @RequestParam(defaultValue = "") String text,
                                                                   @RequestParam(defaultValue = "0") int start,
                                                                   @RequestParam(defaultValue = "10") int limit){

        AdminEventHistoryList response = AdminEventHistoryList.builder()
                .items(adminEventService.searchEventHistory(userId, text, start, limit))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/event-list/count/{userId}")
    public ResponseEntity<Count> searchEventHistoryCount(@PathVariable String userId,
                                                         @RequestParam(defaultValue = "") String text){
        return ResponseEntity.ok(
                adminEventService.searchEventHistoryCount(userId, text)
        );
    }
}
