package com.guide.run.event.entity.dto.response.get;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "공통 개수 응답")
public class Count {
    @Schema(description = "조회 결과 개수", example = "12")
    private long count;
}
