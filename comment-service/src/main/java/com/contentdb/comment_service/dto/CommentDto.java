package com.contentdb.comment_service.dto;

import com.contentdb.comment_service.model.Comment;

import java.time.LocalDateTime;

public class CommentDto {

    private String id;
    private String userId;
    private String contentId;
    private String comment;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    private Integer likes;
    private Integer dislikes;


    public static CommentDto convertToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getUserId(),
                comment.getContentId(),
                comment.getComment(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                comment.getLikeCount(),
                comment.getDislikeCount()
        );
    }

    public CommentDto() {
    }

    public CommentDto(String id, String userId, String contentId, String comment, LocalDateTime createdTime, LocalDateTime updatedTime, Integer likes, Integer dislikes) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.comment = comment;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getContentId() {
        return contentId;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public Integer getLikes() {
        return likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }
}
