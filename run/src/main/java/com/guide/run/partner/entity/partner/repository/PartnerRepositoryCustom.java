package com.guide.run.partner.entity.partner.repository;

import com.guide.run.partner.entity.dto.MyPagePartner;

import java.util.List;

public interface PartnerRepositoryCustom {
    List<MyPagePartner> findMyPartner(String privateId, String sort, int limit, int start, String userType);

    int countMyPartner(String privateId, String userType);
}
