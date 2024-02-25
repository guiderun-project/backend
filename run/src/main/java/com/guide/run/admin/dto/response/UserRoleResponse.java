package com.guide.run.admin.dto.response;

import com.guide.run.user.entity.type.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleResponse {
    private Role role;
}
