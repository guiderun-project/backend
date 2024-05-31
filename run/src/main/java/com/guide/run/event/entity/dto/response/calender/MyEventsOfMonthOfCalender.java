package com.guide.run.event.entity.dto.response.calender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MyEventsOfMonthOfCalender {
    List<MyEventOfMonthOfCalendar> result;
}
