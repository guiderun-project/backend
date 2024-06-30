package com.guide.run.temp.member.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class MatchingTmpDTO {
    private long eventId;
    private long guideId;
    private long viId;
    private String guideRecord;
    private String viRecord;
}
