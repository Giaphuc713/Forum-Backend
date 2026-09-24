package com.backend.Forum.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Integer id;
    private String title;
    private String content;
    private Integer targetId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
