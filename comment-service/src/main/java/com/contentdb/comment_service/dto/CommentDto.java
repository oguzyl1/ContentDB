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


    public static CommentDto convertToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getUserId(),
                comment.getContentId(),
                comment.getComment(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    public CommentDto() {
    }

    public CommentDto(String id, String userId, String contentId, String comment, LocalDateTime createdTime, LocalDateTime updatedTime) {
        this.id = id;
        this.userId = userId;
        this.contentId = contentId;
        this.comment = comment;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime;
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
}
