package com.guide.run.user.repository.withdrawal;

import com.guide.run.user.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, String>, WithdrawalRepositoryCustom {
}
