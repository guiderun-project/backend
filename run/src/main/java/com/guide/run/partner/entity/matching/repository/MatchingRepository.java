package com.guide.run.partner.entity.matching.repository;

import com.guide.run.partner.entity.matching.Matching;
import com.guide.run.partner.entity.matching.MatchingId;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface MatchingRepository extends JpaRepository<Matching, MatchingId>,MatchingRepositoryCustom {
    Matching findByEventIdAndGuideId(long eventId, String guideId);
    Optional<Matching> findByEventIdAndViId(long eventId, String viId);
    Optional<Matching> findByViId(String viId);
    List<Matching> findAllByEventIdAndViId(long eventId,String viId);
    long countByEventIdAndViId(long eventId,String viId);
    void deleteAllByEventId(long eventId);
    List<Matching> findAllByViId(String viId);
    List<Matching> findAllByGuideId(String guideId);
    List<Matching> findAllByEventId(Long eventId);
}
