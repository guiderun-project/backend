package com.guide.run.global.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByPrivateId(String privateId);
}