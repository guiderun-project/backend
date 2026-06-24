package com.guide.run.event.entity.dto.response.get;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.guide.run.user.entity.type.UserType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UpcomingEventResponse {
    private ViewerType viewerType;
    private List<?> items;

    public static UpcomingEventResponse guest(List<GuestItem> items) {
        return UpcomingEventResponse.builder()
                .viewerType(ViewerType.GUEST)
                .items(items)
                .build();
    }

    public static UpcomingEventResponse member(List<MemberItem> items) {
        return UpcomingEventResponse.builder()
                .viewerType(ViewerType.MEMBER)
                .items(items)
                .build();
    }

    public enum ViewerType {
        GUEST,
        MEMBER
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class GuestItem {
        private Long id;
        private String name;
        @Getter(AccessLevel.NONE)
        private int dDay;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate date;

        @JsonProperty("dDay")
        public int getDDay() {
            return dDay;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemberItem {
        private Long id;
        private String name;
        @Getter(AccessLevel.NONE)
        private int dDay;
        private String place;
        private String scheduleText;
        private List<PartnerItem> myPartner;

        @JsonProperty("dDay")
        public int getDDay() {
            return dDay;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PartnerItem {
        private UserType type;
        private String name;
    }
}
