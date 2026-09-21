package com.backend.Forum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.Forum.dto.response.PostResponse;
import com.backend.Forum.dto.response.ForumResponse;
import com.backend.Forum.dto.response.CommentResponse;
import com.backend.Forum.entity.Post;
import com.backend.Forum.entity.Forum;
import com.backend.Forum.entity.Comment;

@Mapper(componentModel = "spring")
public interface ForumMapper {
    @Mapping(target = "forumId", source = "forum.id")
    @Mapping(target = "forumName", source = "forum.name")
    @Mapping(target = "forumAvatarUrl", source = "forum.avatarUrl")
    @Mapping(target = "isPublic", source = "forum.isPublic")
    @Mapping(target = "forumType", source = "forum.type")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.username")
    PostResponse toPostResponse(Post post);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.username")
    @Mapping(target = "type", source = "type")
    ForumResponse toForumResponse(Forum forum);

    @Mapping(target = "postId", source = "post.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.username")
    @Mapping(target = "authorAvatarUrl", source = "author.avatarUrl")
    @Mapping(target = "parentId", source = "parent.id")
    CommentResponse toCommentResponse(Comment comment);
}
