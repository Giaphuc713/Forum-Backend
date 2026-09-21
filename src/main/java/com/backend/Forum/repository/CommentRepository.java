package com.backend.Forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.Forum.entity.Comment;
import com.backend.Forum.entity.Post;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Page<Comment> findByPostAndParentIsNull(Post post, Pageable pageable);

    List<Comment> findByParentId(Integer parentId);
}
