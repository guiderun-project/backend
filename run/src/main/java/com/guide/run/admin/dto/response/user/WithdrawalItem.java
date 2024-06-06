package com.guide.run.admin.dto.response.user;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;

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
    private ArrayList<String> reason;
    private String update_date;
    private String update_time;
}
