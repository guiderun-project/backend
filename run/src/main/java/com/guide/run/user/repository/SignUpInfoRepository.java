package com.guide.run.user.repository;

import com.guide.run.user.entity.SignUpInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignUpInfoRepository extends JpaRepository<SignUpInfo, String> {
}
