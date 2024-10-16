package com.guide.run.event.entity.dto.response.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NotMatchUserInfo {
    private String userId;
    private UserType type;
    private String name;
    private String applyRecord;
    @JsonProperty(value = "isAttended")
    private Boolean isAttended;
    private String recordDegree;
}
