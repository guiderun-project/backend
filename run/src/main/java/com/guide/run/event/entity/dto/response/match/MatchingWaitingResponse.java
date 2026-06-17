package com.guide.run.event.entity.dto.response.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MatchingWaitingResponse {
    private Summary summary;
    private List<MatchingWaitingGroup> groups;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private int waitingCount;
        private int viCount;
        private int guideCount;
    }
}
