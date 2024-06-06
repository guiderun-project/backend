package com.guide.run.admin.dto.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EventApplyCond {
    private boolean time;
    private boolean type_name;
    private boolean team;
}
