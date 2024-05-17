package com.guide.run.user.repository;

import com.guide.run.user.entity.type.Role;
import com.guide.run.user.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,String> {
    Optional<User> findUserByUserId(String uuid);
    Optional<User> findUserByPrivateId(String privateId);
    Page<User> findAllByRoleNot(Role role, Pageable pageable);
    List<User> findAllByRoleNot(Role role);
}

