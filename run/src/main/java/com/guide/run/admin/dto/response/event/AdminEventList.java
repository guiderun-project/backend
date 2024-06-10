package com.guide.run.admin.dto.response.event;

import com.guide.run.admin.dto.EventDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class AdminEventList {
    private List<EventDto> items;
}
