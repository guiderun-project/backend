package com.guide.run.user.repository;

import com.guide.run.user.entity.partner.Partner;
import com.guide.run.user.entity.partner.PartnerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, PartnerId> {

}
