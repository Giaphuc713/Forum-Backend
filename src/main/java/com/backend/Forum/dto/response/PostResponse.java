package com.backend.Forum.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Integer id;
    private Integer forumId;
    private String forumName;
    private String forumAvatarUrl;
    private Boolean isPublic;
    private String forumType;
    private Integer authorId;
    private String authorName;
    private String authorAvatarUrl;
    private String title;
    private String content;
    private String imageUrl;
    private String tags;
    private Integer viewsCount;
    private Integer likesCount;
    private Integer commentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
