package com.guide.run.event.entity.dto.response.get;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventsSummaryGetResponse {
    private PublicSummary publicSummary;
    private MySummary mySummary;

    @Getter
    @Builder
    public static class PublicSummary {
        private int year;
        private long totalEventCount;
        private double totalRunningDistanceKm;
    }

    @Getter
    @Builder
    public static class MySummary {
        private long totalParticipationCount;
        private double totalRunningDistanceKm;
    }
}
