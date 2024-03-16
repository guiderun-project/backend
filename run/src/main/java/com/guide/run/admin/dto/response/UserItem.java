package com.guide.run.admin.dto.response;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserItem {
    private String userId;
    private Role role;
    private int age;
    private UserType type;
    private String name;
    private String team;
    private String gender;
    private String snsId;
    private String phoneNumber;

    private int totalCnt;
    private int trainingCnt;
    private int competitionCnt;

    private LocalDate update_date;
    private String update_time;
}
