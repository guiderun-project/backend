package com.guide.run.admin.dto.response.event;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class AdminEventApplyList {
    private String userId;
    private String role;
    private UserType type;
    private String name;
    private String team;
    private LocalDateTime apply_time;
}
