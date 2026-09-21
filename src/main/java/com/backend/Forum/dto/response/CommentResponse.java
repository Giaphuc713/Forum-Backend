package com.backend.Forum.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
    private Integer id;
    private Integer postId;
    private Integer authorId;
    private String authorName;
    private String content;
    private String authorAvatarUrl;
    private Integer parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> replies;
}
