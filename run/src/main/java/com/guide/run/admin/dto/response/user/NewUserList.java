package com.guide.run.admin.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class NewUserList {
    private List<NewUserResponse> items;
}
