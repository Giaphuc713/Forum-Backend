package com.backend.Forum.security;

import com.backend.Forum.entity.User;
import com.backend.Forum.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
        private final UserRepository userRepository;

        @Override
        @Transactional
        public UserDetails loadUserByUsername(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user: " + email));

                return org.springframework.security.core.userdetails.User.builder()
                                .username(user.getEmail())
                                .password(user.getPassword())
                                .authorities(user.getRoles().stream()
                                                .map(role -> new SimpleGrantedAuthority(
                                                                "ROLE_" + role.getName().name()))
                                                .collect(java.util.stream.Collectors.toList()))
                                .build();
        }

}
