package com.guide.run.admin.dto.response.user;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class WithdrawalItem {
    private String userId;
    private String role;
    private UserType type;
    private String name;
    private String team;
    private String gender;
    private List<String> reason;
    private String update_date;
    private String update_time;

    public WithdrawalItem(String userId,
                          Role role,
                          UserType type,
                          String name,
                          String team,
                          String gender,
                          List<String> reason,
                          String update_date,
                          String update_time) {
        this.userId = userId;
        this.role = role.getValue().substring(5);
        this.type = type;
        this.name = name;
        this.team = team;
        this.gender = gender;
        this.reason = reason;
        this.update_date = update_date;
        this.update_time = update_time;
    }
}


