package com.guide.run.admin.dto.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EventSortCond {
    private boolean time;
    private boolean name;
    private boolean organizer;
    private boolean approval;
}
