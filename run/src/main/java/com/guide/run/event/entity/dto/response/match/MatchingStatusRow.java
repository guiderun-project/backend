package com.guide.run.event.entity.dto.response.match;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MatchingStatusRow {
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private MatchingStatusUser vi;
    private List<MatchingStatusUser> guides;
}
