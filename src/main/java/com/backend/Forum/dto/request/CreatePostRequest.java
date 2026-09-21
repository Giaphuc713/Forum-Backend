package com.backend.Forum.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePostRequest {
    @NotNull(message = "Forum ID is required")
    private Integer forumId;

    @NotNull(message = "Student ID is required")
    private Integer studentId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private String tags;

    private String imageUrl;

    private MultipartFile image;
}