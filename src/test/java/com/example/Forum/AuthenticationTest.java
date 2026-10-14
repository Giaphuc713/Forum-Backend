package com.example.Forum;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.backend.Forum.dto.request.RegisterRequest;
import com.backend.Forum.dto.response.UserResponse;
import com.backend.Forum.entity.Role;
import com.backend.Forum.entity.User;
import com.backend.Forum.repository.PasswordResetTokenRepository;
import com.backend.Forum.repository.RoleRepository;
import com.backend.Forum.repository.UserRepository;
import com.backend.Forum.security.JwtUtils;
import com.backend.Forum.service.AuthService;
import com.backend.Forum.service.RefreshTokenService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationTest {

	@Mock private UserRepository userRepository;
	@Mock private PasswordEncoder passwordEncoder;
	@Mock private RoleRepository roleRepository;
	@Mock private PasswordResetTokenRepository passwordResetTokenRepository;
	@Mock private JavaMailSender mailSender;
	@Mock private AuthenticationManager authenticationManager;
	@Mock private JwtUtils jwtUtils;
	@Mock private RefreshTokenService refreshTokenService;

	@InjectMocks
	private AuthService authService;

	@Test
	void testGetUserName() {
		User user = User.builder()
				.username("Phuc")
				.email("Giaphucha2639@gmail.com")
				.password("Phuc@123")
				.build();

		when(userRepository.findByUsername("Phuc"))
				.thenReturn(java.util.Optional.of(user));

		User result = authService.findByUsername("Phuc");

		assertNotNull(result);
		assertEquals("Phuc", result.getUsername());
	}

	@Test
	void testRegisterUser() {
		RegisterRequest request = new RegisterRequest("Phuc", "Giaphucha2639@gmail.com", "Phuc@123");

		// Bước 1: check trùng → không trùng
		when(userRepository.existsByUsername("Phuc")).thenReturn(false);
		when(userRepository.existsByEmail("Giaphucha2639@gmail.com")).thenReturn(false);

		// Bước 2: tìm role — PHẢI mock, nếu không Set.of(null) → NPE
		Role userRole = Role.builder().name(Role.ERole.USER).build();
		when(roleRepository.findByName(Role.ERole.USER))
				.thenReturn(java.util.Optional.of(userRole));

		// Bước 3: encode password
		when(passwordEncoder.encode("Phuc@123")).thenReturn("encoded_password");

		// Bước 4: save → trả về user đã lưu
		User savedUser = User.builder()
				.id(1)
				.username("Phuc")
				.email("Giaphucha2639@gmail.com")
				.password("encoded_password")
				.roles(java.util.Set.of(userRole))
				.build();
		when(userRepository.save(any(User.class))).thenReturn(savedUser);

		// Execute
		UserResponse result = authService.register(request);

		// Assert
		assertNotNull(result);
		assertEquals("Phuc", result.username());
	}
}