package com.guide.run.user.repository.user;

import com.guide.run.user.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String>, UserRepositoryAdmin {
    Optional<User> findUserByUserId(String uuid);
    Optional<User> findUserByPrivateId(String privateId);

    Optional<User> findUserByPhoneNumber(String phoneNumber);
}

