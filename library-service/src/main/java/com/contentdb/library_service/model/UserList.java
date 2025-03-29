package com.contentdb.library_service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class UserList {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "library_name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic = false;

    @Column(name = "popularity", nullable = false)
    private Integer popularity = 0;

    @Column(name = "content_count", nullable = false)
    private Integer contentCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private UserList(Builder builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.userId = builder.userId;
        this.isPublic = builder.isPublic;
        this.popularity = builder.popularity;
        this.contentCount = builder.contentCount;
    }

    public UserList() {
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


    public void increaseContentCount() {
        this.contentCount++;
    }

    public void decreaseContentCount() {
        if (this.contentCount > 0) {
            this.contentCount--;
        }
    }

    public void increasePopularity() {
        this.popularity++;
    }

    public Integer getContentCount() {
        return contentCount;
    }

    public void setContentCount(Integer contentCount) {
        this.contentCount = contentCount;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLibraryName() {
        return name;
    }

    public void setLibraryName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class Builder {
        private String name;
        private String description;
        private String userId;
        private boolean isPublic = false;
        private Integer popularity = 0;
        private Integer contentCount = 0;

        public Builder(String name) {
            this.name = name;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder popularity(Integer popularity) {
            this.popularity = popularity;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder contentCount(Integer contentCount) {
            this.contentCount = contentCount;
            return this;
        }

        public UserList build() {
            return new UserList(this);
        }
    }

    public static Builder builder(String listName) {
        return new Builder(listName);
    }

    @Override
    public String toString() {
        return "UserList{" +
                "id='" + id + '\'' +
                ", libraryName='" + name + '\'' +
                ", description='" + description + '\'' +
                ", userId='" + userId + '\'' +
                ", isPublic=" + isPublic +
                ", popularity=" + popularity +
                ", contentCount=" + contentCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserList userList = (UserList) o;
        return Objects.equals(id, userList.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
