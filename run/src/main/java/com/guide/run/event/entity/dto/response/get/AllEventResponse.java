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
        @Schema(description = "필터 조건에 해당하는 전체 이벤트 수", example = "42")
        private long totalCount;
    }
}
