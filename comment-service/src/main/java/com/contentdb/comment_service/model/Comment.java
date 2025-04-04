package com.contentdb.comment_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @NotBlank
    @Size(max = 2000)
    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "content_id", nullable = false)
    private String contentId;

    @Column(name = "like_count")
    private Integer likeCount = 0;

    @Column(name = "disslike_count" )
    private Integer dislikeCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    public Comment() {
    }

    private Comment(Builder builder) {
        this.comment = builder.comment;
        this.userId = builder.userId;
        this.contentId = builder.contentId;
        this.likeCount = builder.likeCount;
        this.dislikeCount= builder.dislikeCount;
        this.parentComment = builder.parentComment;
        this.isDeleted = builder.isDeleted;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static class Builder {
        private String comment;
        private String userId;
        private String contentId;
        private Integer likeCount = 0;
        private Integer dislikeCount = 0;
        private Comment parentComment;
        private Boolean isDeleted = false;

        public Builder comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder contentId(String contentId) {
            this.contentId = contentId;
            return this;
        }

        public Builder likeCount(Integer likeCount) {
            this.likeCount = likeCount;
            return this;
        }

        public Builder dislikeCount(Integer dislikeCount) {
            this.dislikeCount = dislikeCount;
            return this;
        }

        public Builder parentComment(Comment parentComment) {
            this.parentComment = parentComment;
            return this;
        }

        public Builder isDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
            return this;
        }

        public Comment build() {
            return new Comment(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }


    public String getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getDislikeCount() {
        return dislikeCount;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setDislikeCount(Integer dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }


    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", comment='" + comment + '\'' +
                ", userId='" + userId + '\'' +
                ", contentId='" + contentId + '\'' +
                ", likeCount=" + likeCount +
                ", dislikeCount=" + dislikeCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isDeleted=" + isDeleted +
                ", parentComment=" + parentComment +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment1 = (Comment) o;
        return Objects.equals(id, comment1.id) && Objects.equals(comment, comment1.comment) && Objects.equals(userId, comment1.userId) && Objects.equals(contentId, comment1.contentId) && Objects.equals(likeCount, comment1.likeCount) && Objects.equals(dislikeCount, comment1.dislikeCount) && Objects.equals(createdAt, comment1.createdAt) && Objects.equals(updatedAt, comment1.updatedAt) && Objects.equals(isDeleted, comment1.isDeleted) && Objects.equals(parentComment, comment1.parentComment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comment, userId, contentId, likeCount, dislikeCount, createdAt, updatedAt, isDeleted, parentComment);
    }
}
