package com.guide.run.event.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventRecruitStatus {
    UPCOMING("모집예정"),
    OPEN("모집중"),
    CLOSE("모집마감"),
    END("이벤트종료");
    private final String value;
}
