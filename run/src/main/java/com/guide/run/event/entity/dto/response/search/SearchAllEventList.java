package com.guide.run.event.entity.dto.response.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
public class SearchAllEventList {
    private List<SearchAllEvent> items;
}
