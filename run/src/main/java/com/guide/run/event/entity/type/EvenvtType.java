package com.guide.run.event.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EvenvtType {
    Training("훈련"),
    Competition("대회");
    private final String value;
}
