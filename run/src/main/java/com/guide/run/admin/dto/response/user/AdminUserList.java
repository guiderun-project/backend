package com.guide.run.admin.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "관리자 회원 목록 응답")
public class AdminUserList {
    @Schema(description = "회원 목록")
    private List<UserItem> items;
}
