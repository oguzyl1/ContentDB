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

//
//    // Bonus: Kullanıcıya ait kütüphanelerden içerik sorgulama
//    UserList<UserListContent> findByLibrary_UserIdAndContentId(String userId, String contentId);
//
//    // Bonus: Kütüphanedeki içerikleri sıralı getirme
//    UserList<UserListContent> findByLibraryOrderByDisplayOrderAsc(UserList library);
//
//
//    UserList<UserListContent> findByLibrary(UserList library);
//
//    UserList<UserListContent> findByLibraryId(Long libraryId);
//
//
//    UserList<UserListContent> findByLibraryAndDisplayOrderBetween(UserList library, int startOrder, int endOrder);
//
//    Page<UserListContent> findByLibrary(UserList library, Pageable pageable);
//
//    void deleteByLibrary(UserList library);


}
