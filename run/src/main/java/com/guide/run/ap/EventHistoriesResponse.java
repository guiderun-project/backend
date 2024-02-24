package com.guide.run.ap;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class EventHistoriesResponse {
    private List<EventHistory> items;
}
