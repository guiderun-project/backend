package com.guide.run.admin.dto.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class WithdrawalSortCond {
    private boolean time;
    private boolean type;
    private boolean gender;
    private boolean name_team;
}
