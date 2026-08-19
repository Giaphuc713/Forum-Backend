package com.backend.Forum.service;

import com.backend.Forum.dto.request.LoginRequest;
import com.backend.Forum.dto.request.RegisterRequest;
import com.backend.Forum.dto.response.AuthResponse;
import com.backend.Forum.dto.response.UserResponse;
import com.backend.Forum.entity.Role;
import com.backend.Forum.entity.User;
import com.backend.Forum.repository.UserRepository;
import com.backend.Forum.repository.RoleRepository;
import com.backend.Forum.security.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Service
@Transactional
@RequiredArgsConstructor

public class AuthService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final RoleRepository roleRepository;

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;

        @Transactional
        public UserResponse register(RegisterRequest request) {
                if (userRepository.existsByUsername(request.username())) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Username đã tồn tại");
                }
                if (userRepository.existsByEmail(request.email())) {
                        throw new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "Email đã tồn tại");
                }
                Role userRole = roleRepository.findByName(Role.ERole.USER)
                                .orElseGet(() -> roleRepository.save(
                                                Role.builder().name(Role.ERole.USER).build()));
                User user = User.builder()
                                .username(request.username())
                                .email(request.email())
                                .password(passwordEncoder.encode(request.password()))
                                .roles(java.util.Set.of(userRole))
                                .build();

                return toResponse(userRepository.save(user));
        }

        private UserResponse toResponse(User user) {
                return new UserResponse(
                                user.getId(),
                                user.getUsername(), // Username là String, không gọi .name()
                                user.getEmail(),
                                user.getRoles().stream()
                                                .map(role -> role.getName().name())
                                                .collect(java.util.stream.Collectors.joining(",")) // Chuyển đổi
                                                                                                   // Set<Role> thành
                                                                                                   // String
                );
        }

        public AuthResponse login(LoginRequest request) {
                try {
                        authenticationManager
                                        .authenticate(new UsernamePasswordAuthenticationToken(request.email(),
                                                        request.password()));
                } catch (BadCredentialsException e) {
                        throw new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Sai email đăng nhập hoặc mật khẩu");
                }
                User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy email"));
                return new AuthResponse(jwtService.generateToken(user.getEmail()), user.getEmail(),
                                user.getRoles().stream()
                                                .map(role -> role.getName().name())
                                                .collect(java.util.stream.Collectors.joining(",")));
        }

        public UserResponse getCurrentUser(String email) {
                return toResponse(userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Không tìm thấy email")));
        }

}
