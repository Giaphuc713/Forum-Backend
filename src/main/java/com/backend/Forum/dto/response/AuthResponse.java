package com.backend.Forum.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private Integer id;
    private String fullName;
    private String email;
    private List<String> roles;

    public AuthResponse(String accessToken, String refreshToken, Integer id, String fullName, String email,
            List<String> roles) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
    }
}