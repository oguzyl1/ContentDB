package com.contentdb.comment_service.model;

import jakarta.persistence.*;

@Entity
public class UserCommentInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "interaction_type", nullable = false)
    private InteractionType interactionType;

    public UserCommentInteraction() {
    }

    private UserCommentInteraction(Builder builder) {
        this.userId = builder.userId;
        this.comment = builder.comment;
        this.interactionType = builder.interactionType;
    }

    public static class Builder {
        private String userId;
        private Comment comment;
        private InteractionType interactionType;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder comment(Comment comment) {
            this.comment = comment;
            return this;
        }

        public Builder interactionType(InteractionType interactionType) {
            this.interactionType = interactionType;
            return this;
        }

        public UserCommentInteraction build() {
            return new UserCommentInteraction(this);

        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public InteractionType getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(InteractionType interactionType) {
        this.interactionType = interactionType;
    }
}