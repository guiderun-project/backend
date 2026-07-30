package com.guide.run.event.entity.dto.response.search;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SearchAllEvent {
    private Long id;
    private EventRecruitStatus recruitStatus;
    private String name;
    private EventType type;
    private String dateText;
}
