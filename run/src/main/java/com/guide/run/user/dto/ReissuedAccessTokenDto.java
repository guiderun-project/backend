package com.guide.run.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReissuedAccessTokenDto {
    private String accessToken;
}
