package com.guide.run.event.entity.dto.response.match;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MatchingStatusUser {
    private String userId;
    private String name;
    private UserType type;
    private String applyGroup; //이벤트 참가 신청 시 입력한 희망 그룹
    private String defaultGroup; //회원가입 시 입력한 기본 그룹
}
