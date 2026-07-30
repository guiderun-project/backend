package com.guide.run.event.entity.dto.response.get;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "전체 이벤트 목록 응답")
public class AllEventResponse {
    @Schema(description = "이벤트 목록")
    private List<AllEvent> items;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "페이지네이션 정보")
    public static class Pagination {
        @Schema(description = "현재 페이지 번호(1부터 시작)", example = "1")
        private int page;

        @Schema(description = "페이지 크기", example = "10")
        private int size;

        @Schema(description = "필터 조건에 해당하는 전체 이벤트 수", example = "42")
        private long totalCount;

        @Schema(description = "전체 페이지 수", example = "5")
        private int totalPages;

        @Schema(description = "다음 페이지 존재 여부", example = "true")
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
