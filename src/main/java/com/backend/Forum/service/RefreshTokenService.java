package com.backend.Forum.service;

import com.backend.Forum.entity.RefreshToken;
import com.backend.Forum.entity.User;
import com.backend.Forum.exception.AcademicException;
import com.backend.Forum.repository.RefreshTokenRepository;
import com.backend.Forum.repository.UserRepository;
import com.backend.Forum.security.JwtUtils;
import com.backend.Forum.security.StudentDetailsImplementation;
import lombok.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${jwt.refreshTokenExpirationMs}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken createRefreshToken(Integer userId) {
        RefreshToken refreshToken = new RefreshToken();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // delete refresh token if user is already exists
        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
        StudentDetailsImplementation userDetails = StudentDetailsImplementation.build(user);

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(jwtUtils.generateRefreshToken(userDetails));
        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new AcademicException(HttpStatus.UNAUTHORIZED,
                    "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenRepository.deleteByUser(user);

    }

}
