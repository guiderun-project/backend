package com.guide.run.admin.dto.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AllSortCond {

    private boolean event_time;
    private boolean event_approval;
    private boolean name;
    private boolean organizer;

    private boolean user_time;
    private boolean type;
    private boolean gender;
    private boolean name_team;
    private boolean user_approval;
}
