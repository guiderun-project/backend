package com.guide.run.event.entity.dto.response.attend;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AttendanceParticipant {
    private String userId;
    private String name;
    private UserType type;
    @JsonProperty("isFirstParticipation")
    @Builder.Default
    private Boolean isFirstParticipation = false;
}
