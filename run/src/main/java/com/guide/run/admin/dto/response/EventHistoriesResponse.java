package com.guide.run.admin.dto.response;

import com.guide.run.admin.dto.EventHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class EventHistoriesResponse {
    private List<EventHistoryDto> items;
}
