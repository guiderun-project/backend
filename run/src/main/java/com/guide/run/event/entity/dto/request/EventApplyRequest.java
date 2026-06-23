package com.guide.run.event.entity.dto.request;

import com.guide.run.event.entity.type.AdditionalQuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventApplyRequest {
    private String group;
    private String partner;
    private String detail;
    private CompetitionApplicationInfo competitionInfo;
    private List<AdditionalAnswerRequest> additionalAnswers;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitionApplicationInfo {
        private LocalDate birthDate;
        private String phoneNumber;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalAnswerRequest {
        private Long questionId;
        private AdditionalQuestionType type;
        private String answerText;
        private Long selectedOptionId;
    }
}
