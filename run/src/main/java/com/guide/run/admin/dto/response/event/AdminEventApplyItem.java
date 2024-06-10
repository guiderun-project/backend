package com.guide.run.admin.dto.response.event;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AdminEventApplyItem {
    private String userId;
    private String role;
    private UserType type;
    private String name;
    private String team;
    private LocalDateTime apply_time;

    public AdminEventApplyItem(String userId,
                               Role role,
                               UserType type,
                               String name,
                               String team,
                               LocalDateTime apply_time) {
        this.userId = userId;
        this.role = role.getValue();
        this.type = type;
        this.name = name;
        this.team = team;
        this.apply_time = apply_time;
    }
}
