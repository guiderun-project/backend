package com.guide.run.event.entity.dto.response.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guide.run.event.service.EventTemporalStatusResolver;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
public class MyEventDday {
    String name;
    @JsonProperty(value = "dDay")
    Long dDay;

    public MyEventDday(String name, LocalDateTime dDay) {
        this.name = name;
        this.dDay = ChronoUnit.DAYS.between(
                EventTemporalStatusResolver.today(),
                dDay.toLocalDate()
        );
    }
}
