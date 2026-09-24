package com.backend.Forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.backend.Forum.entity.ForumMembership;
import com.backend.Forum.entity.Forum;

import java.util.List;
import java.util.Optional;

public interface ForumMembershipRepository extends JpaRepository<ForumMembership, Integer> {
        boolean existsByForum_IdAndStudent_Id(Integer ForumId, Integer StudentId);

        boolean existsByForum_IdAndStudent_IdAndStatus(
                        Integer ForumId, Integer StudentId,
                        ForumMembership.MembershipStatus Status);

        boolean existsByForum_IdAndStudent_IdAndMembershipRole(Integer forumId, Integer studentId,
                        ForumMembership.MembershipRole role);

        List<ForumMembership> findByForum_Id(Integer forumId);

}
