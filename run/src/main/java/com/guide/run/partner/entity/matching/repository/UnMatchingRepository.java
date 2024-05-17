package com.guide.run.partner.entity.matching.repository;

import com.guide.run.partner.entity.matching.UnMatching;
import com.guide.run.partner.entity.matching.UnMatchingId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnMatchingRepository extends JpaRepository<UnMatching, UnMatchingId> {
}
