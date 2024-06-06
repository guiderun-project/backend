package com.guide.run.admin.dto.response.user;

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
    private String img;
    private String role;
    private int age;
    private String type;
    private String name;
    private String team;
    private String gender;
    private String snsId;
    private String phoneNumber;

    private int totalCnt;
    private int trainingCnt;
    private int competitionCnt;

    private String update_date;
    private String update_time;

    public UserItem(String userId,
                    String img,
                    Role role,
                    int age,
                    UserType type,
                    String name,
                    String team,
                    String gender,
                    String snsId,
                    String phoneNumber,
                    int totalCnt,
                    int trainingCnt,
                    int competitionCnt,
                    String update_date,
                    String update_time) {
        this.userId = userId;
        this.img = img;
        this.role = role.getValue();
        this.age = age;
        this.type = type.getValue();
        this.name = name;
        this.team = team;
        this.gender = gender;
        this.snsId = snsId;
        this.phoneNumber = phoneNumber;
        this.totalCnt = totalCnt;
        this.trainingCnt = trainingCnt;
        this.competitionCnt = competitionCnt;
        this.update_date = update_date;
        this.update_time = update_time;
    }
}
