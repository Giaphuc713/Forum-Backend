package com.backend.Forum.security;

import com.backend.Forum.entity.User;
import com.backend.Forum.repository.UserRepository;
import lombok.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {
    private final UserRepository userRepository;

    public boolean isCurrentUser(Integer userId) {
        if (userId == null) {
            return false;
        }
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).map(user -> user.getId().equals(userId)).orElse(false);
    }
}
