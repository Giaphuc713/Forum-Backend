package com.backend.Forum.entity;

import jakarta.persistence.*;

import lombok.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_memberships", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "forum_id", "student_id" }) })
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ForumMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student ;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MembershipRole membershipRole = MembershipRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MembershipStatus status = MembershipStatus.APPROVED;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime joinedAt;

    public enum MembershipRole {
        OWNER, ADMIN, MEMBER
    }

    public enum MembershipStatus {
        PENDING, APPROVED, REJECTED
    }
}
