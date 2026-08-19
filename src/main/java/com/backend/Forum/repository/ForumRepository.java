package com.backend.Forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.Forum.entity.Forum;

public interface ForumRepository extends JpaRepository<Forum, Integer> {
}
