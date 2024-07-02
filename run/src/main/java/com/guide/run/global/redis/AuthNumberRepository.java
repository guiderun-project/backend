package com.guide.run.global.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthNumberRepository extends CrudRepository<AuthNumber, String> {
    Optional<AuthNumber> findByAuthNum(String authNum);
    void deleteAuthNumberByAuthNum(String authNum);
}
