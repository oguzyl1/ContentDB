package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void deleteRefreshTokenByUserId(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}