package com.backend.Forum.listener;

import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.backend.Forum.entity.Post;
import com.backend.Forum.entity.Forum;
import com.backend.Forum.entity.User;
import com.backend.Forum.entity.Comment;
import com.backend.Forum.entity.ForumMembership;
import com.backend.Forum.entity.Notification;
import com.backend.Forum.repository.ForumMembershipRepository;
import com.backend.Forum.repository.NotificationRepository;
import com.backend.Forum.event.NotificationEvent;

import java.util.List;

import lombok.*;

@Component
@AllArgsConstructor
@Slf4j
@Builder
public class NotificationEventListener {
    private final NotificationRepository notificationRepository;
    private final ForumMembershipRepository forumMembershipRepository;
    private final JavaMailSender javaMailSender;

    @Async
    @EventListener
    @Transactional
    public void handlePostCreated(NotificationEvent.PostCreated event) {
        Post post = event.post();
        String tags = post.getTags() != null ? post.getTags() : "";
        boolean isCtsv = tags.contains("Phòng CTSV") || tags.contains("Phòng Công tác sinh viên");

        // ONLY notify if it's a CTSV post
        if (!isCtsv) {
            log.info("Regular post created, skipping global notifications.");
            return;
        }

        // Get members of the forum for CTSV notifications
        List<ForumMembership> memberships = forumMembershipRepository.findByForum_Id(post.getForum().getId());

        for (ForumMembership membership : memberships) {
            User student = membership.getStudent();
            // Don't notify the author
            if (student.getId().equals(post.getAuthor().getId()))
                continue;

            // 1. Save DB notification for real-time
            saveNotification(student,
                    "Thông báo quan trọng từ CTSV",
                    "Bài viết: " + post.getTitle(),
                    "URGENT",
                    post.getId());
        }
    }

    @Async
    @EventListener
    @Transactional
    public void handleCommentCreated(NotificationEvent.CommentCreated event) {
        Comment comment = event.comment();
        Post post = comment.getPost();
        User author = post.getAuthor();
        User commentUser = comment.getAuthor();

        // Notify author if someone comment on his post
        if (!author.getId().equals(commentUser.getId())) {
            saveNotification(author,
                    "Bình luận mới",
                    commentUser.getUsername() + " đã bình luận về bài viết của bạn: " + post.getTitle(),
                    "COMMENT",
                    comment.getId());
        }
        // 2. Notify parent comment author if this's a reply
        if (comment.getParent() != null) {
            User parentAuthor = comment.getParent().getAuthor();
            if (!parentAuthor.getId().equals(commentUser.getId()) && !parentAuthor.getId().equals(author.getId())) {
                saveNotification(parentAuthor,
                        "Phản hồi bình luận",
                        commentUser.getUsername() + " đã phản hồi bình luận của bạn trong bài viết: "
                                + post.getTitle(),
                        "REPLY",
                        comment.getId());
            }
        }

    }

    private void saveNotification(User user, String title, String content, String type,
            Integer targetId) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .content(content)
                .type(type)
                .targetId(targetId)
                .build();
        notificationRepository.save(notification);
    }

}