package com.guide.run.event.entity.dto.response.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MatchingCreateResponse {
    private String viId;
    private List<String> guideIds;
    private Summary summary;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private int waitingCount;
        private int completedViCount;
        private int matchedGuideCount;
    }
}
