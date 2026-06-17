package com.guide.run.event.entity.dto.response.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MatchingCompletedResponse {
    private Summary summary;
    private List<MatchingCompletedGroup> groups;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private int completedViCount;
        private int matchedGuideCount;
    }
}
