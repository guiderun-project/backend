package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "훈련 참여 및 안전 면책 동의 요청")
public class TrainingSafetyAgreementRequest {

    @NotNull
    @AssertTrue
    @Schema(description = "훈련 참여 및 안전 면책 동의 여부 (true 필수)", example = "true")
    private Boolean trainingSafety;
}
