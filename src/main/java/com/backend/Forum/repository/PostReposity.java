package com.backend.Forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.backend.Forum.entity.Post;

import java.time.LocalDateTime;
import java.util.List;

public interface PostReposity extends JpaRepository<Post, Integer> {
    Page<Post> findByForumId(Integer forumId, Pageable page);

    Page<Post> findByAuthorId(Integer studentId, Pageable page);

    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable page);

    @Query("SELECT p FROM Post p WHERE LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%'))")
    Page<Post> findByTag(@Param("tag") String tag, Pageable page);

    @Query("SELECT p FROM Post p ORDER BY p.viewCount DESC, p.likesCount DESC")
    Page<Post> findTrendingPosts(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.createdAt >= :startDate")
    List<Post> findRecentPosts(@Param("startDate") LocalDateTime startDate);

}
