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
import com.backend.Forum.dto.request.UpdatePostRequest;
import com.backend.Forum.dto.response.PostResponse;
import com.backend.Forum.dto.request.CreateForumRequest;
import com.backend.Forum.dto.response.ForumResponse;
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

    @PreAuthorize("isAuthenticated()")
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

    @PreAuthorize("@securityUtils.isCurrentUser(#authorId) or hasRole('ADMIN')")
    @DeleteMapping("/posts/{postId}")
    @Tag(name = "2. Posts")
    @Operation(summary = "Delete a post by owner or admin")
    public ApiResponse<String> deletePostByOwner(@PathVariable Integer postId, @RequestParam Integer authorId) {
        forumService.deletePostByOwner(postId, authorId);
        return ApiResponse.success("Delete Post successfully");
    }

    @PreAuthorize("@securityUtils.isCurrentUser(#authorId) or hasRole('ADMIN')")
    @PutMapping(value = "/posts/{postId}", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "2. Posts")
    @Operation(summary = "Update a post by owner")
    public ApiResponse<PostResponse> updatePost(
            @PathVariable Integer postId,
            @RequestParam Integer authorId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        String imageUrl = null;
        if (imageUrl != null || !imageUrl.isEmpty()) {
            imageUrl = storageService.uploadImage(image);
        }
        UpdatePostRequest request = UpdatePostRequest.builder()
                .title(title)
                .content(content)
                .tags(tags)
                .build();
        return ApiResponse.success(forumService.updatePost(postId, authorId, request, imageUrl));

    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "1. Forums")
    @Operation(summary = "Create a new forum (tạo forum mới)")
    public ApiResponse<ForumResponse> createForum(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "rules", required = false) String rules,
            @RequestParam("isPublic") Boolean isPublic,
            @RequestParam("authorId") Integer authorId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        String avatarUrl = null;
        if (image != null && !image.isEmpty()) {
            avatarUrl = storageService.uploadImage(image);
        }
        CreateForumRequest request = CreateForumRequest.builder()
                .name(name)
                .description(description)
                .rules(rules)
                .isPublic(isPublic)
                .authorId(authorId)
                .avatarUrl(avatarUrl)
                .build();
        return ApiResponse.success(forumService.createForum(request));
    }

    @GetMapping
    @Tag(name = "1. Forums")
    @Operation(summary = "Get all forums")
    public ApiResponse<List<ForumResponse>> getAllForums(@RequestParam(required = false) Integer studentId) {
        return ApiResponse.success(forumService.getAllForums(studentId));
    }

    @PreAuthorize("@securityUtils.isCurrentUser(#authorId) or hasRole('ADMIN')")
    @PutMapping(value = "/{forumId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Tag(name = "1. Forums")
    @Operation(summary = "Update a forum by owner or admin")
    public ApiResponse<ForumResponse> updateForum(
            @PathVariable Integer forumId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "rules", required = false) String rules,
            @RequestParam(value = "isPublic", required = false) Boolean isPublic,
            @RequestParam(value = "authorId") Integer authorId, // use for authentication
            @RequestParam(value = "image", required = false) MultipartFile image) {
        String avatarUrl = null;
        if (image != null && !image.isEmpty()) {
            avatarUrl = storageService.uploadImage(image);
        }
        CreateForumRequest request = CreateForumRequest.builder()
                .name(name)
                .description(description)
                .rules(rules)
                .isPublic(isPublic)
                .authorId(authorId)
                .avatarUrl(avatarUrl)
                .build();
        return ApiResponse.success(forumService.updateForum(forumId, request, isPublic));
    }

    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#authorId)")
    @DeleteMapping("/{forumId}")
    @Tag(name = "1. Forums")
    @Operation(summary = "Delete a forum by owner or admin")
    public ApiResponse<String> deleteForum(@PathVariable Integer forumId, @RequestParam Integer authorId) {
        forumService.deleteForum(forumId, authorId);
        return ApiResponse.success("Delete Forum successfully");
    }

}
