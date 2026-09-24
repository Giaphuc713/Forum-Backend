package com.backend.Forum.event;

import com.backend.Forum.entity.Forum;
import com.backend.Forum.entity.Post;
import com.backend.Forum.entity.User;
import com.backend.Forum.entity.Comment;

public interface NotificationEvent {
    record PostCreated(Post post) {
    };

    record CommentCreated(Comment comment) {
    };

    record PostLiked(User userLiked, Post post) {
    };

    record StudentRequestToJoin(Forum forum, User studentRequested) {
    };

    record StudentApproved(Forum forum, User user) {
    };
}
