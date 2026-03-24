package com.guide.run.event.entity.dto.response.match;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "미매칭 사용자 유형별 개수 응답")
public class UserTypeCount {
    @Schema(description = "미매칭 VI 수", example = "2")
    private long vi;
    @Schema(description = "미매칭 Guide 수", example = "3")
    private long guide;
}
