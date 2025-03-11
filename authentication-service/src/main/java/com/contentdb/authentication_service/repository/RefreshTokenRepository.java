package com.contentdb.authentication_service.repository;

import com.contentdb.authentication_service.model.RefreshToken;
import com.contentdb.authentication_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    Optional<RefreshToken> findByUserId(String userId);

    @Transactional
    void deleteByUserId(String userId);
}
