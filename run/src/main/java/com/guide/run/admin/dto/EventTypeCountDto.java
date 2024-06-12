package com.guide.run.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EventTypeCountDto {
    private int totalCnt;
    private int trainingCnt;
    private int contestCnt;
}
