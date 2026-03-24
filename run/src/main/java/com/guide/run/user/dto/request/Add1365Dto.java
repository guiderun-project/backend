package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "1365 아이디 저장 요청")
public class Add1365Dto {
    @Schema(description = "저장할 1365 아이디", example = "guide1365")
    private String id1365;
}
