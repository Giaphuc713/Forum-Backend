package com.backend.Forum.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @NotNull(message = "StudentId is required")
    private Integer studentId;

    @NotNull(message = "PostId is required")
    private Integer postId;

    @NotBlank(message = "Content is required")
    private String content;

    // Null for a top-level comment; set to a comment ID when replying.
    @Schema(nullable = true, defaultValue = "null")
    private Integer parentId;
}
