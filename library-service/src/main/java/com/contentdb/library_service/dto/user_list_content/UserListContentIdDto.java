package com.contentdb.library_service.dto.user_list_content;

import com.contentdb.library_service.model.UserListContent;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;


public class UserListContentIdDto  {

    private String listId;
    private String listName;
    private List<String> contentIds;


    public static UserListContentIdDto convertToUserListContentIdDto(UserListContent content) {
        return new UserListContentIdDto(
                content.getUserList().getId(),
                content.getUserList().getName(),
                List.of(content.getContentId())
        );
    }


    public UserListContentIdDto() {
    }

    public UserListContentIdDto(String listId, String listName, List<String> contentIds) {
        this.listId = listId;
        this.listName = listName;
        this.contentIds = contentIds;
    }

    public String getListId() {
        return listId;
    }

    public String getListName() {
        return listName;
    }

    public List<String> getContentIds() {
        return contentIds;
    }
}
