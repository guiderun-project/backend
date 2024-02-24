package com.guide.run.admin.dto.response;

import com.guide.run.admin.dto.EventDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class AllEventListResponse {
    private List<EventDto> items;
}
