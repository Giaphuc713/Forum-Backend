package com.backend.Forum.dto.response;

import lombok.*;
import com.backend.Forum.entity.ForumMembership;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForumMemberResponse {
    private Integer studentId;
    private String fullName;
    private String avatarUrl;
    private String email;
    private String role;
    private String status;
    private String requestMessage;
    private LocalDateTime joinedAt;
}
