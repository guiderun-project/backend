package com.guide.run.event.entity.dto.response.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class SearchAllEventsCount {
    private int count;
}
