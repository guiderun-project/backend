package com.guide.run.partner.entity.partner.repository;

import com.guide.run.partner.entity.partner.Partner;
import com.guide.run.partner.entity.partner.PartnerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, PartnerId>, PartnerRepositoryCustom {
}
