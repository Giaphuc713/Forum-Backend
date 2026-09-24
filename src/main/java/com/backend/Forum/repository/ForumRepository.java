package com.backend.Forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.Forum.entity.Forum;

import java.util.Optional;
import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Integer> {
    Optional<Forum> findByInvitationCode(String invitationCode);

    List<Forum> findByAuthorId(Integer authorId);
}
