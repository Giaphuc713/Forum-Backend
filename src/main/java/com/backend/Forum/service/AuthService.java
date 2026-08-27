package com.backend.Forum.service;

import com.backend.Forum.dto.request.LoginRequest;
import com.backend.Forum.dto.request.RegisterRequest;
import com.backend.Forum.dto.request.ForgotPasswordRequest;
import com.backend.Forum.dto.request.ResetPasswordRequest;
import com.backend.Forum.dto.response.AuthResponse;
import com.backend.Forum.dto.response.UserResponse;
import com.backend.Forum.entity.Role;
import com.backend.Forum.entity.User;
import com.backend.Forum.exception.AcademicException;
import com.backend.Forum.entity.PasswordResetToken;
import com.backend.Forum.repository.UserRepository;
import com.backend.Forum.repository.RoleRepository;
import com.backend.Forum.repository.PasswordResetTokenRepository;
import com.backend.Forum.security.JwtService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor

public class AuthService {
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final RoleRepository roleRepository;
        private final PasswordResetTokenRepository passwordResetTokenRepository;
        private final JavaMailSender mailSender;
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

        @Transactional
        public void forgotPassword(ForgotPasswordRequest request) {
                User user = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND,
                                                "Không tìm thấy email"));
                // Delete old token if exists
                passwordResetTokenRepository.deleteByUser(user);
                // Generate new token
                String token = UUID.randomUUID().toString();
                PasswordResetToken resetToken = PasswordResetToken.builder()
                                .user(user)
                                .token(token)
                                .expiresAt(Instant.now().plusSeconds(3600))
                                .build();
                passwordResetTokenRepository.save(resetToken);
                // Send email
                sendResetPasswordEmail(user.getEmail(), user.getUsername(), token);
        }

        private void sendResetPasswordEmail(String email, String username, String token) {
                try {
                        MimeMessage message = mailSender.createMimeMessage();
                        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                        helper.setTo(email);
                        helper.setSubject("RESET YOUR PASSWORD");

                        String content = "<h3>Hello " + username + ",</h3>"
                                        + "<p>You have requested to reset your password. Use the code below to reset it:</p>"
                                        + "<h2 style='color: #2e6da4;'>" + token + "</h2>"
                                        + "<p>This code will expire in 1 hour.</p>"
                                        + "<p>If you didn't request this, please ignore this email.</p>";

                        helper.setText(content, true);
                        mailSender.send(message);
                } catch (Exception e) {
                        e.printStackTrace();
                        throw new AcademicException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send reset password");
                }
        }

        @Transactional
        public void resetPassword(ResetPasswordRequest request) {
                PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "Token không tồn tại"));
                User user = resetToken.getUser();
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                // delete token after use
                passwordResetTokenRepository.delete(resetToken);
        }

}
