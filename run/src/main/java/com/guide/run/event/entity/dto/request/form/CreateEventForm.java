package com.guide.run.event.entity.dto.request.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
@Schema(description = "이벤트 신청서 생성/수정 요청")
public class CreateEventForm {
    @Schema(description = "희망 그룹 또는 페이스", example = "A")
    private String group;
    @Schema(description = "희망 파트너 이름", example = "김가이드")
    private String partner;
    @Schema(description = "추가 요청사항 및 상세 내용", example = "대회용 티셔츠는 M 사이즈 희망")
    private String detail;
}
