package com.guide.run.event.entity.dto.response.attend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ParticipationInfos {
    private List<ParticipationInfo> attend;
    private List<ParticipationInfo> notAttend;
}
