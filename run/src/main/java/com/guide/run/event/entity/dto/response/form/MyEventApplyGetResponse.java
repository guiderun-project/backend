package com.guide.run.event.entity.dto.response.form;

import com.guide.run.event.entity.dto.request.EventApplyRequest;
import com.guide.run.event.entity.type.AdditionalQuestionType;
import com.guide.run.event.entity.type.EventCategory;
import com.guide.run.event.entity.type.EventType;
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
public class MyEventApplyGetResponse {
    private Long eventId;
    private String eventName;
    private EventType eventType;
    private EventCategory eventCategory;
    private UserType userType;
    private String name;
    private String recordDegree;
    private ApplicationInfo applicationInfo;
    private EventApplyRequest.CompetitionApplicationInfo competitionInfo;
    private List<AdditionalAnswerDetail> additionalAnswers;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApplicationInfo {
        private String group;
        private String partner;
        private String detail;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdditionalAnswerDetail {
        private Long questionId;
        private AdditionalQuestionType type;
        private String question;
        private String answerText;
        private Long selectedOptionId;
        private String selectedOptionValue;
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
