package com.guide.run.partner.entity.partner.repository;

import com.guide.run.admin.dto.response.partner.AdminPartnerResponse;
import com.guide.run.partner.entity.dto.MyPagePartner;
import com.guide.run.user.entity.type.UserType;

import java.util.List;

public interface PartnerRepositoryCustom {
    List<MyPagePartner> findMyPartner(String privateId, String sort, int limit, int start, UserType userType);

    long countMyPartner(String privateId, UserType userType);

    List<AdminPartnerResponse> getAdminPartner(String privateId, UserType type, String kind, int limit, int start);

    long countAdminPartner(String privateId, UserType type, String kind);

    List<AdminPartnerResponse> searchAdminPartner(String privateId, UserType type, String text, int limit, int start);
    long searchAdminPartnerCount(String privateId, UserType type, String text);
}
