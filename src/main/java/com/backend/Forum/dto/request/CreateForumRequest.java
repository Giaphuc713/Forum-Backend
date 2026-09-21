package com.backend.Forum.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateForumRequest {
    @NotBlank(message = "Forum name is required")
    private String name;

    private String description;

    private boolean isPublic;

    @NotNull(message = "Author ID is required")

    private Integer authorId;

    private String rules;

    private String avatarUrl;

}
