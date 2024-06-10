package com.guide.run.event.entity.dto.response.match;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NotMatchUserInfo {
    private String userId;
    private UserType type;
    private String name;
}