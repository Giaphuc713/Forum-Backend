package com.backend.Forum.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import com.backend.Forum.dto.request.CreatePostRequest;
import com.backend.Forum.dto.response.PostResponse;
import com.backend.Forum.dto.request.UpdatePostRequest;
import com.backend.Forum.dto.request.CreateForumRequest;
import com.backend.Forum.dto.response.ForumResponse;
import com.backend.Forum.entity.Forum;
import com.backend.Forum.entity.ForumMembership;
import com.backend.Forum.entity.Post;
import com.backend.Forum.entity.Role;
import com.backend.Forum.entity.User;
import com.backend.Forum.exception.AcademicException;
import com.backend.Forum.exception.ResourceNotFoundException;
import com.backend.Forum.mapper.ForumMapper;
import com.backend.Forum.repository.ForumRepository;
import com.backend.Forum.repository.PostReposity;
import com.backend.Forum.repository.UserRepository;
import com.backend.Forum.repository.UserRepository;
import com.backend.Forum.repository.ForumMembershipRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ForumService {
        private final PostReposity postRepository;
        private final UserRepository userRepository;
        private final ForumRepository forumRepository;
        private final ForumMapper forumMapper;
        private final ForumMembershipRepository forumMembershipRepository;

        private static final String DEFAULT_RULES = "1. Tôn trọng các thành viên khác.\n2. Không đăng nội dung phản cảm, kích động.\n3. Không quảng cáo, spam.\n4. Thảo luận văn minh, tôn trọng ý kiến đóng góp.";

        public Page<PostResponse> getPostByForum(Integer forumId, Integer studentId, Pageable pageable) {
                Forum forum = forumRepository.findById(forumId)
                                .orElseThrow(() -> new AcademicException(HttpStatus.NOT_FOUND, "Forum not found"));

                // if (!isVisibleToStudent(forum, studentId)) {
                // throw new AcademicException(HttpStatus.FORBIDDEN, "You do not have
                // permission" + "to view posts in this forum");
                // }
                return postRepository.findByForumId(forumId, pageable).map(forumMapper::toPostResponse);
        }

        @Transactional
        public PostResponse createPost(CreatePostRequest request) {
                Forum forum = forumRepository.findById(request.getForumId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Forum not found with ID: " + request.getForumId()));
                User user = userRepository.findById(request.getStudentId())
                                .orElseThrow(
                                                () -> new ResourceNotFoundException("Student not found with ID: "
                                                                + request.getStudentId()));

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

        @Transactional
        public PostResponse updatePost(Integer postID, Integer authorId, UpdatePostRequest request, String imageUrl) {
                Post post = postRepository.findById(postID)
                                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + postID));
                User user = userRepository.findById(authorId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Author not found with ID: " + authorId));
                post.setTitle(request.getTitle());
                post.setContent(request.getContent());
                post.setTags(request.getTags());

                if (imageUrl != null) {
                        post.setImageUrl(imageUrl);
                }
                return forumMapper.toPostResponse(postRepository.save(post));
        };

        @Transactional
        public void deletePostByOwner(Integer postId, Integer authorId) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new AcademicException(HttpStatus.NOT_FOUND, "Post not found"));

                if (!post.getAuthor().getId().equals(authorId)) {
                        throw new AcademicException(HttpStatus.NOT_FOUND, "You can't Delete not your post");
                }

                postRepository.delete(post);
        }

        @Transactional
        public ForumResponse createForum(CreateForumRequest request) {
                User user = userRepository.findById(request.getAuthorId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User not found with ID: " + request.getAuthorId()));

                Forum forum = Forum.builder()
                                .name(request.getName())
                                .description(request.getDescription())
                                .rules(!request.getRules().isEmpty() ? request.getRules() : DEFAULT_RULES)
                                .isPublic(request.isPublic())
                                .type(request.isPublic() ? Forum.ForumType.GLOBAL : Forum.ForumType.PRIVATE)
                                .author(user)
                                .membersCount(1)
                                .avatarUrl(request.getAvatarUrl())
                                .build();
                Forum savedForum = forumRepository.save(forum);

                // // Auto-join the author as OWNER
                ForumMembership membership = ForumMembership.builder()
                                .forum(savedForum)
                                .student(user)
                                .membershipRole(ForumMembership.MembershipRole.OWNER)
                                .build();
                forumMembershipRepository.save(membership);
                return forumMapper.toForumResponse(savedForum);
        }

        public List<ForumResponse> getAllForums(Integer studentId) {
                return forumRepository.findAll().stream()
                                .filter(forum -> isVisibleToStudent(forum, studentId))
                                .map(forumMapper::toForumResponse)
                                .collect(Collectors.toList());
        }

        public boolean isVisibleToStudent(Forum forum, Integer studentId) {
                if (forum.getType() == Forum.ForumType.GLOBAL || Boolean.TRUE.equals(forum.getIsPublic())) {
                        return true;
                }
                if (studentId == null) {
                        return false;
                }
                // Must be approved to see private forum
                return forumMembershipRepository.existsByForum_IdAndStudent_IdAndStatus(
                                forum.getId(),
                                studentId,
                                ForumMembership.MembershipStatus.APPROVED);
        }

        public List<ForumResponse> getForumsByAuthor(Integer authorId) {
                return forumRepository.findByAuthorId(authorId).stream()
                                .map(forumMapper::toForumResponse)
                                .collect(Collectors.toList());
        }

        @Transactional
        public ForumResponse updateForum(Integer forumId, CreateForumRequest request, Boolean isPublic) {
                Forum forum = forumRepository.findById(forumId).orElseThrow(
                                () -> new ResourceNotFoundException("Forum not found with ID: " + forumId));
                if (request.getAuthorId() == null || !forum.getAuthor().getId().equals(request.getAuthorId())) {
                        throw new AcademicException(HttpStatus.NOT_FOUND, "Only owner can update their forum");
                }
                if (request.getName() != null && !request.getName().trim().isEmpty()) {
                        forum.setName(request.getName());
                }
                if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                        forum.setDescription(request.getDescription());
                }
                if (request.getRules() != null && !request.getRules().trim().isEmpty()) {
                        forum.setRules(request.getRules());
                }
                if (isPublic != null) {
                        forum.setIsPublic(isPublic);
                }
                if (request.getAvatarUrl() != null) {
                        forum.setAvatarUrl(request.getAvatarUrl());
                }
                return forumMapper.toForumResponse(forumRepository.save(forum));
        }

        @Transactional
        public void deleteForum(Integer forumId, Integer authorId) {
                Forum forum = forumRepository.findById(forumId).orElseThrow(
                                () -> new ResourceNotFoundException("Forum not found with ID: " + forumId));

                if (!forum.getAuthor().getId().equals(authorId)) {
                        throw new AcademicException(HttpStatus.NOT_FOUND, "Only owner can delete their forum");
                }
                // Delete forum will casual delete all posts, comments, and likes in that forum
                forumRepository.delete(forum);
        }

}
