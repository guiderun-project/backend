package com.guide.run.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FindAccountIdDto {
    private String accountId;
    private String createdAt;
}
