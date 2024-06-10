package com.guide.run.admin.dto.response.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AdminEventApplyList {
    private List<AdminEventApplyItem> items;
}
