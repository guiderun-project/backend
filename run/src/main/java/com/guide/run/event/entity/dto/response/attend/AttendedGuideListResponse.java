package com.guide.run.event.entity.dto.response.attend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendedGuideListResponse {
    private List<AttendedGuide> items;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendedGuide {
        private String name;
        private String birthDate;
        private String id1365;
    }
}
