package com.guide.run.admin.dto.response.event;

import com.guide.run.admin.dto.EventHistoryDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AdminEventHistoryList {
    private List<EventHistoryDto> items;
}
