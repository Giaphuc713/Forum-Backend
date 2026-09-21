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

    @NotNull(message = "Content is required")
    private String content;

    private Integer parentId; // Optional parentId
}
