package com.guide.run.admin.dto.response;

import com.guide.run.admin.dto.EventDto;
import com.guide.run.admin.dto.response.user.UserItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AdminSearchList {
    private List<UserItem> user_items;
    private List<EventDto> event_items;
}
