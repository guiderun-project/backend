package com.guide.run.event.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventCategory {
    GENERAL("일반"),
    GROUP("그룹별"),
    TEAM("팀별");
    private final String value;
}
