package com.guide.run.event.entity.dto.response;

import com.guide.run.event.entity.type.AdditionalQuestionType;
import com.guide.run.event.entity.type.EventCategory;
import com.guide.run.event.entity.type.EventRecruitStatus;
import com.guide.run.event.entity.type.EventType;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDetailResponse {
    private Long eventId;
    private String name;
    private EventType eventType;
    private EventCategory eventCategory;
    private EventRecruitStatus recruitStatus;
    private boolean isPrivate;
    private LocalDate recruitStartDate;
    private LocalDate recruitEndDate;
    private Organizer organizer;
    private Schedule schedule;
    private String place;
    private BigDecimal expectedRunningDistanceKm;
    private String content;
    private List<AdditionalQuestion> additionalQuestions;
    private Viewer viewer;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Organizer {
        private String name;
        private UserType type;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Schedule {
        private String date;
        private String startTime;
        private String endTime;
        private String dateText;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Viewer {
        private boolean isApplied;
        private boolean isOrganizer;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdditionalQuestion {
        private Long questionId;
        private AdditionalQuestionType type;
        private String question;
        private List<Option> options;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private Long optionId;
        private String value;
    }
}
