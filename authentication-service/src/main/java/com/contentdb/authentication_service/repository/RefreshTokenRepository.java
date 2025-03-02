package com.contentdb.authentication_service.repository;

import com.contentdb.authentication_service.model.RefreshToken;
import com.contentdb.authentication_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);

    List<RefreshToken> findByUser(User user);
}
