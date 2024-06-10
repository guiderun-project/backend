package com.guide.run.admin.dto.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EventApplyCond {
    private int time;
    private int type_name;
    private int team;
}
