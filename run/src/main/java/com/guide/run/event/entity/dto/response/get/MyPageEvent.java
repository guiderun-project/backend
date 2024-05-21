package com.guide.run.event.entity.dto.response.get;

import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageEvent {
    private Long eventId;
    private EventType eventType;
    private String name;
    private String date;
    private EventRecruitStatus recruitStatus;
}
