package com.guide.run.event.entity.dto.response.match;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MatchingWaitingGroup {
    private String runningGroup;
    private int totalCount;
    private List<MatchingWaitingParticipant> participants;
}
