package com.guide.run.admin.dto.response.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class CurrentEventList {
    private List<CurrentEventResponse> items;
}
