package com.guide.run.event.entity.dto.response.form;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventApplicantListResponse {
    private Summary summary;
    private List<EventApplicantGroup> groups;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Summary {
        private long totalCount;
        private long viCount;
        private long guideCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventApplicantGroup {
        private String runningGroup;
        private long totalCount;
        private List<EventApplicant> applicants;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EventApplicant {
        private String userId;
        private String name;
        private UserType type;
        private boolean isFirstParticipation;
    }
}
