package com.guide.run.event.entity.dto.response.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guide.run.user.entity.type.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MatchingWaitingParticipant {
    private String userId;
    private String name;
    private UserType type;
    private String originalRunningGroup;
    @JsonProperty("isFirstParticipation")
    private Boolean isFirstParticipation;
    private String hopePartner;
    private String additionalComment;
    private List<AdditionalAnswer> additionalAnswers;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class AdditionalAnswer {
        private Long questionId;
        private String questionTitle;
        private String questionType;
        private String answer;
    }
}
