package com.backend.Forum.controller;

import com.backend.Forum.dto.request.LoginRequest;
import com.backend.Forum.dto.request.RegisterRequest;
import com.backend.Forum.dto.response.AuthResponse;
import com.backend.Forum.dto.response.UserResponse;
import com.backend.Forum.dto.request.ForgotPasswordRequest;
import com.backend.Forum.dto.request.ResetPasswordRequest;
import com.backend.Forum.service.AuthService;
import com.backend.Forum.dto.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@RequestBody @Valid RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password request (Quên mật khẩu)")
    public ApiResponse<String> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.success("Password reset request sent successfully");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password (Đặt lại mật khẩu)")
    public ApiResponse<String> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.success("Password reset successful");
    }
}
