package com.guide.run.partner.entity.partner.repository;

import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.user.entity.type.UserType;

import java.util.List;

public interface PartnerRepositoryCustom {
    List<Partner> findActivityPartners(String privateId, UserType userType, String sort, int page, int size);
    long countActivityPartners(String privateId, UserType userType);
}
