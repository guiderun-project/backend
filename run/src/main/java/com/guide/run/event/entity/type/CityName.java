package com.guide.run.event.entity.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CityName {
    SEOUL("서울"),
    BUSAN("부산");
    
    private String value;
}
