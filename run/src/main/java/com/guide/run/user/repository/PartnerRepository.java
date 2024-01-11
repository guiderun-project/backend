package com.guide.run.user.repository;

import com.guide.run.user.entity.Partner;
import com.guide.run.user.entity.PartnerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, PartnerId> {

}
