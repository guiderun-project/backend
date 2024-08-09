package com.guide.run.partner.entity.matching.repository;

import com.guide.run.event.entity.dto.response.match.NotMatchUserInfo;
import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.UnMatchingId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UnMatchingRepository extends JpaRepository<UnMatching, UnMatchingId> ,UnMatchingRepositoryCustom{
    Optional<UnMatching> findByPrivateIdAndEventId(String privateId,Long eventId);
    void deleteAllByEventId(long eventId);
    void deleteAllByPrivateId(String privateId);

    List<UnMatching> findAllByPrivateId(String privateId);

}
