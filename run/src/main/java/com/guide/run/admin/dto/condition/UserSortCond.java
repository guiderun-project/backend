package com.guide.run.admin.dto.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSortCond {
    private boolean time;
    private boolean type;
    private boolean gender;
    private boolean name_team;
    private boolean approval;
}
