package com.guide.run.admin.dto.response.event;

import com.guide.run.admin.dto.EventDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@Schema(description = "관리자 이벤트 목록 응답")
public class AdminEventList {
    @Schema(description = "이벤트 목록")
    private List<EventDto> items;
}
