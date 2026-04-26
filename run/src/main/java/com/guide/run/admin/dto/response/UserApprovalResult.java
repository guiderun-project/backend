package com.guide.run.admin.dto.response;

import com.guide.run.user.entity.type.Role;

public record UserApprovalResult(
        String userId,
        Role role,
        String recordDegree,
        boolean changed
) {
}
