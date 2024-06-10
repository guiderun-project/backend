package com.guide.run.partner.entity.matching.repository;

import com.guide.run.event.entity.dto.response.match.NotMatchUserInfo;
import com.guide.run.user.entity.type.UserType;

import java.util.List;

public interface UnMatchingRepositoryCustom {
    long getUserTypeCount(Long eventId, UserType userType);
    List<NotMatchUserInfo> findNotMatchUserInfos(Long eventId);
}
