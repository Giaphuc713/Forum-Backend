package com.backend.Forum.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.backend.Forum.dto.request.CreatePostRequest;
import com.backend.Forum.dto.response.PostResponse;
import com.backend.Forum.entity.User;
import com.backend.Forum.dto.response.ApiResponse;
import com.backend.Forum.service.ForumService;
import com.backend.Forum.service.StorageService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/forums")
@RequiredArgsConstructor
@Validated
public class ForumController {

    private final ForumService forumService;
    private final StorageService storageService;

    @GetMapping("/{forumId}/posts")
    @Tag(name = "2. Posts")
    @Operation(summary = "Get posts by forum category (Support Private Check)")
    public ApiResponse<Page<PostResponse>> getPostByForum(
            @PathVariable Integer forumId,
            @RequestParam(required = false) Integer studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(
                forumService.getPostByForum(forumId, studentId,
                        PageRequest.of(page, size, Sort.by("id").descending())));
    }

    @PreAuthorize("isAuthenticated() and (#request.studentId == principal.id or hasAuthority('ADMIN'))")
    @PostMapping(value = "/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "2. Posts")
    @Operation(summary = "Create a new post with optional image ( tạo bài viết kèm ảnh)")
    public ApiResponse<PostResponse> createPost(@Valid CreatePostRequest request) {
        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageUrl = storageService.uploadImage(request.getImage());
            request.setImageUrl(imageUrl);
        }
        return ApiResponse.success(forumService.createPost(request));
    }

}
