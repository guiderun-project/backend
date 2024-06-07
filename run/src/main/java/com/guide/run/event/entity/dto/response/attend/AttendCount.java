package com.guide.run.event.entity.dto.response.attend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AttendCount {
    private Long attend;
    private Long notAttend;
}
