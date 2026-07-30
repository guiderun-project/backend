package com.guide.run.event.entity.dto.response.form;

import com.guide.run.event.entity.type.AdditionalQuestionType;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventApplicantFormResponse {
    private Applicant applicant;
    private Form form;
    private List<AdditionalAnswer> additionalAnswers;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Applicant {
        private String userId;
        private String name;
        private UserType type;
        private LocalDate birthDate;
        private String phoneNumber;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Form {
        private String applyGroup;
        private String hopePartner;
        private String additionalComment;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdditionalAnswer {
        private Long questionId;
        private String questionTitle;
        private AdditionalQuestionType questionType;
        private String answer;
    }
}
