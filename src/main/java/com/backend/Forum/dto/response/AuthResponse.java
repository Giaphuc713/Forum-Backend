package com.backend.Forum.dto.response;

public record AuthResponse(String token, String email, String role) {
}