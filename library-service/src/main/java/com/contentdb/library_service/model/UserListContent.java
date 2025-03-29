package com.contentdb.library_service.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_list_id", "content_id"},
                name = "uk_library_content"
        )
)
public class UserListContent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_list_id", nullable = false)
    private UserList userList;

    @Column(name = "content_id", nullable = false)
    private String contentId;

    @Column(name = "media_type", nullable = false)
    private String mediaType;

    @Column(name = "order_number")
    private Integer orderNumber;

    @Column(name = "created_by")
    private String createdBy; // Kim tarafından oluşturulduğu bilgisi

    @Column(name = "updated_by")
    private String updatedBy; // Kim tarafından güncellendiği bilgisi

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public UserListContent() {
    }

    private UserListContent(Builder builder) {
        this.id = builder.id;
        this.userList = builder.userList;
        this.contentId = builder.contentId;
        this.mediaType = builder.mediaType;
        this.orderNumber = builder.orderNumber;
        this.createdBy = builder.createdBy;
        this.updatedBy = builder.updatedBy;
    }


    public String getMediaType() {
        return mediaType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserList getUserList() {
        return userList;
    }

    public void setUserList(UserList userList) {
        this.userList = userList;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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
        private String id;
        private final UserList userList;
        private final String contentId;
        private String mediaType;
        private Integer orderNumber;
        private String createdBy;
        private String updatedBy;

        public Builder(UserList userList, String contentId) {
            this.userList = userList;
            this.contentId = contentId;
        }

        public Builder orderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }


        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder updatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
            return this;
        }

        public Builder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public UserListContent build() {
            return new UserListContent(this);
        }
    }

    public static Builder builder(UserList userList, String contentId) {
        return new Builder(userList, contentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserListContent that = (UserListContent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
