package com.guide.run.event.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventRecruitStatus {
    RECRUIT_UPCOMING("모집예정"),
    RECRUIT_OPEN("모집중"),
    RECRUIT_CLOSE("모집마감"),
    RECRUIT_END("이벤트종료"),
    RECRUIT_ALL("전체현황");
    private final String value;
}
