package com.guide.run.global.redis;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthNumberRepository extends CrudRepository<AuthNumber, String> {
    // 인증번호 검증은 반드시 세션(verificationId) 단위로만 조회한다.
    // 전화번호 무관 전역 조회(findByAuthNum)는 무차별 대입에 악용될 수 있어 의도적으로 두지 않는다.
    Optional<AuthNumber> findByVerificationId(String verificationId);
}
