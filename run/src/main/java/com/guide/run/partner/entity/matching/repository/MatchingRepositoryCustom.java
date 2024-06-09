package com.guide.run.partner.entity.matching.repository;

import com.guide.run.event.entity.dto.response.match.MatchedGuideInfo;
import com.guide.run.event.entity.dto.response.match.MatchedViInfo;
import com.guide.run.user.entity.type.UserType;

import java.util.List;

public interface MatchingRepositoryCustom {
    List<MatchedGuideInfo> findAllMatchedGuideByEventIdAndViId(Long EventId,String viId);
    List<MatchedViInfo> findAllMatchedViByEventIdAndUserType(Long eventId, UserType userType);
}
