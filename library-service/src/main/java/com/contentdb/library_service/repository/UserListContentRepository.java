package com.contentdb.library_service.repository;

import com.contentdb.library_service.model.UserList;
import com.contentdb.library_service.model.UserListContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserListContentRepository extends JpaRepository<UserListContent, String> {

    boolean existsByUserListAndContentId(UserList userList, String contentId);

    Integer countByUserList(UserList userList);

    Optional<UserListContent> findByUserListAndContentId(UserList listName, String contentId);

    List<UserListContent> findByUserListAndOrderNumberGreaterThan(UserList userList, Integer orderNumber);

    List<UserListContent> findByUserListId(String id);

}
