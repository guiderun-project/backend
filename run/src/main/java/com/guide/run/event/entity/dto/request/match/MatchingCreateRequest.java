package com.guide.run.event.entity.dto.request.match;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MatchingCreateRequest {
    private String viId;
    private List<String> guideIds;
}
