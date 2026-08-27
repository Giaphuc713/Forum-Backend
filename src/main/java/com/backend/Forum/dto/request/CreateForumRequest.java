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
    @Schema(description = "Tên forum", example = "Cộng đồng Java HCMUS")
    private String name;

    @Schema(description = "Mô tả forum", example = "Nơi thảo luận về Java và Spring Boot")
    private String description;

    @Schema(description = "Trạng thái công khai", example = "true")
    private boolean isPublic;

    @NotNull(message = "Author ID is required")
    @Schema(description = "ID sinh viên tạo forum", example = "1")
    private Integer authorId;

    @Schema(description = "Quy tắc của forum", example = "1. Tôn trọng lẫn nhau. 2. Không spam...")
    private String rules;

    @Schema(description = "URL ảnh đại diện forum", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

}
