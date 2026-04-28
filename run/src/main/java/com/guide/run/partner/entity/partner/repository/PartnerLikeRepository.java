package com.guide.run.partner.entity.partner.repository;

import com.guide.run.partner.entity.partner.PartnerLike;
import com.guide.run.partner.entity.partner.PartnerLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartnerLikeRepository extends JpaRepository<PartnerLike, PartnerLikeId>, PartnerLikeRepositoryCustom {
    Optional<PartnerLike> findByRecIdAndSendId(String partnerId, String privateId);
    void deleteAllByRecId(String privateId);
    void deleteAllBySendId(String privateId);
}
