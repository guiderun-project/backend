package com.guide.run.event.entity.dto.response.attend;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AttendanceUpdateResponse {
    private String userId;
    @JsonProperty("isAttended")
    private Boolean isAttended;
    private Summary summary;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private Long waitingCount;
        private Long attendedCount;
    }
}
