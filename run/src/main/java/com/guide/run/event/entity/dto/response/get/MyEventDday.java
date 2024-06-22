package com.guide.run.event.entity.dto.response.get;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class MyEventDday {
    String name;
    long dDay;

    public MyEventDday(String name, LocalDateTime dDay) {
        this.name = name;
        this.dDay = LocalDateTime.now().until(dDay, ChronoUnit.DAYS);
    }
}
