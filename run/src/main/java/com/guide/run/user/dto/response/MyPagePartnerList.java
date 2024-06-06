package com.guide.run.user.dto.response;

import com.guide.run.partner.entity.dto.MyPagePartner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class MyPagePartnerList {
    private List<MyPagePartner> items;
}
