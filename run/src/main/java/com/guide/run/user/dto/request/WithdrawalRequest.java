package com.guide.run.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 탈퇴 요청")
public class WithdrawalRequest {
    @Schema(description = "선택한 탈퇴 사유 목록", example = "[\"서비스를 거의 이용하지 않아요\", \"원하는 이벤트가 없어요\"]")
    private List<String> reasons = new ArrayList<>();
}
