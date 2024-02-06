package com.guide.run.event.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EventType {
    TRAINING("훈련"),
    COMPETITION("대회");
    private final String value;
}
