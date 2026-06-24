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
        private int page;
        private int size;
        private long totalCount;
        private int totalPages;
        private boolean hasNext;

        public static Pagination of(int page, int size, long totalCount) {
            int normalizedPage = Math.max(page, 1);
            int totalPages = size > 0 ? (int) Math.ceil((double) totalCount / size) : 0;
            return Pagination.builder()
                    .page(normalizedPage)
                    .size(size)
                    .totalCount(totalCount)
                    .totalPages(totalPages)
                    .hasNext(normalizedPage < totalPages)
                    .build();
        }
    }
}
