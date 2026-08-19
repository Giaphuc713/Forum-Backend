package com.backend.Forum.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.backend.Forum.dto.request.CreatePostRequest;
import com.backend.Forum.dto.response.PostResponse;
import com.backend.Forum.entity.Forum;
import com.backend.Forum.entity.Post;
import com.backend.Forum.entity.Role;
import com.backend.Forum.entity.User;
import com.backend.Forum.exception.ResourceNotFoundException;
import com.backend.Forum.mapper.ForumMapper;
import com.backend.Forum.repository.ForumRepository;
import com.backend.Forum.repository.PostReposity;
import com.backend.Forum.repository.UserRepository;
import com.backend.Forum.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForumService {
    private final PostReposity postRepository;
    private final UserRepository userRepository;
    private final ForumRepository forumRepository;
    private final ForumMapper forumMapper;

    public Page<PostResponse> getPostByForum(Integer forumId, Integer studentId, Pageable pageable) {
        // Forum forum = forumRepository.findById(forumId)
        // .orElseThrow(() -> new AcademicException(HttpStatus.NOT_FOUND, "Forum not
        // found"));

        // if (!isVisibleToStudent(forum, studentId)) {
        // throw new AcademicException(HttpStatus.FORBIDDEN, "You do not have
        // permission" +
        // "to view posts in this forum");
        // }
        return postRepository.findByForumId(forumId, pageable).map(forumMapper::toPostResponse);
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request) {
        Forum forum = forumRepository.findById(request.getForumId())
                .orElseThrow(() -> new ResourceNotFoundException("Forum not found with ID: " + request.getForumId()));
        User user = userRepository.findById(request.getStudentId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Student not found with ID: " + request.getStudentId()));

        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ADMIN);
        Post post = Post.builder()
                .forum(forum)
                .author(user)
                .title(request.getTitle())
                .content(request.getContent())
                .tags(request.getTags())
                .imageUrl(request.getImageUrl())
                .build();

        Post savedPost = postRepository.save(post);
        return forumMapper.toPostResponse(savedPost);
    }

}
