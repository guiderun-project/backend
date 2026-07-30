package com.guide.run.event.entity.dto.response.form;

import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCanceledApplicantListResponse {
    private Summary summary;
    private List<CanceledApplicant> canceledApplicants;

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
    public static class CanceledApplicant {
        private String userId;
        private String name;
        private UserType type;
        private LocalDateTime canceledAt;
    }
}
