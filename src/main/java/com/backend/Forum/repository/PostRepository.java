package com.backend.Forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.backend.Forum.entity.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query("SELECT p FROM Post p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);

    Page<Post> findByForumId(Integer forumId, Pageable pageable);

    Page<Post> findByAuthorId(Integer authorId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE LOWER(p.tags) LIKE LOWER(CONCAT('%', :tag, '%'))")
    Page<Post> findByTag(@Param("tag") String tag, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY p.viewCount DESC, p.likesCount DESC")
    Page<Post> findByTopTrending(Pageable pageable);

    @Query("SELECT p FROM Post p where p.createdAt >= :startDate")
    Page<Post> findByMostRecent(Pageable pageable);
}
