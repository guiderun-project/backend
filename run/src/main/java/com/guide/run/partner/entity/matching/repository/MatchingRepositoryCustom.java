package com.guide.run.partner.entity.matching.repository;

import com.guide.run.event.entity.dto.response.match.MatchingCompletedFlatDto;

import java.util.List;

public interface MatchingRepositoryCustom {
    long countDistinctViByEventId(Long eventId);
    List<MatchingCompletedFlatDto> findMatchingCompletedByEventId(Long eventId);
}
