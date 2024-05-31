package com.guide.run.event.entity.dto.response.calender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MyEventOfMonthOfCalendar {
    int day;
    boolean competition;
    boolean training;
}
