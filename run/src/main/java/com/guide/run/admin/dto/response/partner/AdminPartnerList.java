package com.guide.run.admin.dto.response.partner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class AdminPartnerList {
    private List<AdminPartnerResponse> items;
}
