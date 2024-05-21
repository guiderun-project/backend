package com.guide.run.event.entity.dto.response.get;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageEventResponse {
    private int start;
    private int limit;
    private List<MyPageEvent> items;
}
