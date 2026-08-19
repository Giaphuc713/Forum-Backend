package com.backend.Forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.Forum.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
