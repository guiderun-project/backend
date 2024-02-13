package com.guide.run.user.repository;

import com.guide.run.user.entity.Guide;
import com.guide.run.user.entity.User;
import com.guide.run.user.entity.Vi;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findUserByUserId(String uuid);
    Optional<User> findUserByPrivateId(String privateId);
}

