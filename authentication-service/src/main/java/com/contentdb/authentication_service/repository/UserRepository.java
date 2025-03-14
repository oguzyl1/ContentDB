package com.contentdb.authentication_service.repository;

import com.contentdb.authentication_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.username = :username")
    Optional<String> findUserIdByUsername(@Param("username") String username);

    @Query("SELECT COUNT(u) FROM User u JOIN u.authorities a WHERE a = 'ROLE_ADMIN'")
    long countAdmins();

    List<User> findByLastLoginTimeBefore(LocalDateTime time);

    Optional<User> findByUsernameOrEmail(String username, String email);

}
