package com.contentdb.library_service.repository;

import com.contentdb.library_service.model.UserList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserListRepository extends JpaRepository<UserList, String> {

    Optional<UserList> findByNameAndUserId(String libraryName, String userId);

    List<UserList> findByUserId(String userId);

    @Query("SELECT l FROM UserList l WHERE l.userId = :userId AND l.isPublic = true")
    List<UserList> findByUserIdAndIsPublicTrue(String userId);

    List<UserList> findTop10ByOrderByPopularityDesc();

    Optional<UserList> findByName(String listName);

    List<UserList> findTop10ByIsPublicTrueOrderByPopularityDesc();
}
