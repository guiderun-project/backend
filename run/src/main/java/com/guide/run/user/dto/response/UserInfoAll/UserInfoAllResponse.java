package com.guide.run.user.dto.response.UserInfoAll;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoAllResponse {
    private List<GuideInfoAllResponse> guideInfo;
    private List<ViInfoAllResponse> viInfo;
}
