package com.contentdb.library_service.dto.user_list;

import com.contentdb.library_service.model.UserList;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;


public class UserListDto {

    private String id;
    private String name;
    private String userId;
    private String description;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @JsonProperty("public")
    private boolean isPublic;

    private Integer popularity;
    private Integer contentCount;

    public static UserListDto convertToLibraryDto(UserList userList) {
        return new UserListDto(
                userList.getId(),
                userList.getLibraryName(),
                userList.getDescription(),
                userList.getCreatedAt(),
                userList.getUpdatedAt(),
                userList.getUserId(),
                userList.isPublic(),
                userList.getPopularity(),
                userList.getContentCount()
        );
    }

    public UserListDto() {
    }

    public UserListDto(String id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt, String userId, boolean isPublic, Integer popularity, Integer contentCount) {
        this.id = id;
        this.name = name;
        this.userId = userId;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isPublic = isPublic;
        this.popularity = popularity;
        this.contentCount = contentCount;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public Integer getContentCount() {
        return contentCount;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @JsonProperty("public")
    public boolean isPublic() {
        return isPublic;
    }
}
