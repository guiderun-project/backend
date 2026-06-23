package com.guide.run.event.entity.dto.response.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Builder
@AllArgsConstructor
public class SearchAllEventList {
    private List<SearchAllEvent> items;
    private Pagination pagination;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pagination {
        private long totalCount;
    }
}
