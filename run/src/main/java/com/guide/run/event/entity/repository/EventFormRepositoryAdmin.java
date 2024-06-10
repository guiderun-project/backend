package com.guide.run.event.entity.repository;

import com.guide.run.admin.dto.condition.EventApplyCond;
import com.guide.run.admin.dto.response.event.AdminEventApplyItem;

import java.util.List;

public interface EventFormRepositoryAdmin {
    List<AdminEventApplyItem> getEventApplyList(long eventId, EventApplyCond cond, int start, int limit);
    long getEventApplyCount(long eventId);
}
