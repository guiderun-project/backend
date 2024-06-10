package com.guide.run.admin.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserList {
    private List<UserItem> items;
}
