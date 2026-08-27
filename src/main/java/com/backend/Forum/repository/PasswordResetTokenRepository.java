package com.backend.Forum.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.Forum.entity.PasswordResetToken;
import com.backend.Forum.entity.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
