package com.guide.run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PermissionDto {
    private boolean privacy;
    private boolean portraitRights;
}
