package com.guide.run.user.repository;

import com.guide.run.user.entity.SignUpInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SignUpInfoRepository extends JpaRepository<SignUpInfo, String> {
    Optional<SignUpInfo> findByAccountId(String accountId);
}
