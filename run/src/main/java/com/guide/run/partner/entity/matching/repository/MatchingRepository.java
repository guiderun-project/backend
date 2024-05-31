package com.guide.run.partner.entity.matching.repository;

import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.MatchingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchingRepository extends JpaRepository<Matching, MatchingId> {
    Matching findByEventIdAndGuideId(long eventId, String guideId);
    Matching findByEventIdAndViId(long eventId, String viId);
}
