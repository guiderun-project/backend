package com.guide.run.global.redis;

import org.springframework.data.repository.CrudRepository;

public interface TmpTokenRepository extends CrudRepository<TmpToken, String> {
}
