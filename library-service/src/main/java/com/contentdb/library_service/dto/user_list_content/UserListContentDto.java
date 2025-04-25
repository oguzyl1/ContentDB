package com.contentdb.library_service.dto.user_list_content;

import com.contentdb.library_service.model.UserListContent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;


public class UserListContentDto  {

    private String id;
    private String listId;
    private String listName;
    private String contentId;
    private String mediaType;
    private Integer orderNumber;
    private LocalDateTime createdAt;

    public UserListContentDto(String id, String listId, String listName, String contentId,String mediaType, Integer orderNumber, LocalDateTime createdAt) {
        this.id = id;
        this.listId = listId;
        this.listName = listName;
        this.contentId = contentId;
        this.mediaType = mediaType;
        this.orderNumber = orderNumber;
        this.createdAt = createdAt;
    }

    public UserListContentDto() {
    }

    public static UserListContentDto convertToUserListContentDto(UserListContent userListContent) {
        return new UserListContentDto(
                userListContent.getId(),
                userListContent.getUserList().getId(),
                userListContent.getUserList().getLibraryName(),
                userListContent.getContentId(),
                userListContent.getMediaType(),
                userListContent.getOrderNumber(),
                userListContent.getCreatedAt());
    }

    public String getId() {
        return id;
    }

    public String getListId() {
        return listId;
    }

    public String getListName() {
        return listName;
    }

    public String getContentId() {
        return contentId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public String getMediaType() {
        return mediaType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
