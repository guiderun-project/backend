package com.guide.run.event.entity.dto.response.get;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "전체 이벤트 목록 응답")
public class AllEventResponse {
    @Schema(description = "이벤트 목록")
    private List<AllEvent> items;
}
