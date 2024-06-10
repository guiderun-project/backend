package com.guide.run.admin.dto.response.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AbsentDto {
    private long absent;
    private long viAbsent;
    private long guideAbsent;
}
