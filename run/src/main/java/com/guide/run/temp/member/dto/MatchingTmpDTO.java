package com.guide.run.temp.member.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class MatchingTmpDTO {
    private long eventId;
    private String guideId;
    private String viId;
    private String guideRecord;
    private String viRecord;
}
