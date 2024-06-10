package com.guide.run.admin.controller;

import com.guide.run.admin.dto.condition.WithdrawalSortCond;
import com.guide.run.admin.dto.response.user.WithdrawalList;
import com.guide.run.admin.service.AdminWithdrawalService;
import com.guide.run.event.entity.dto.response.get.Count;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminWithdrawalController {
    private final AdminWithdrawalService adminWithDrawalService;

    @GetMapping("/withdrawal-list")
    public ResponseEntity<WithdrawalList> getWithdrawalList(@RequestParam(defaultValue = "0") int start,
                                                            @RequestParam(defaultValue = "10") int limit,
                                                            @RequestParam(defaultValue = "2") int time,
                                                            @RequestParam(defaultValue = "2") int type,
                                                            @RequestParam(defaultValue = "2") int gender,
                                                            @RequestParam(defaultValue = "2") int name_team){
        WithdrawalSortCond cond = WithdrawalSortCond.builder()
                .name_team(name_team)
                .gender(gender)
                .type(type)
                .time(time)
                .build();
        WithdrawalList response = WithdrawalList.builder()
                .items(adminWithDrawalService.getWithdrawalList(start,limit,cond))
                .build();

        return ResponseEntity.ok(response);

    }

    @GetMapping("/withdrawal-list/count")
    public ResponseEntity<Count> getWithdrawalCount(){
        return ResponseEntity.ok(adminWithDrawalService.getWithdrawalCount());
    }
}
