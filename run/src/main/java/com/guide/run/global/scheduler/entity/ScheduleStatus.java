package com.guide.run.global.scheduler.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduleStatus {
    PENDING,
    OPEN,
    END
}
