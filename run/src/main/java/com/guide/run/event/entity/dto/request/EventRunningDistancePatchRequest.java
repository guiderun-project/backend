package com.guide.run.event.entity.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventRunningDistancePatchRequest {
    private BigDecimal expectedRunningDistanceKm;
}
