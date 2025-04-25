package com.contentdb.authentication_service.security;

import com.contentdb.authentication_service.service.JwtService;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlacklist {

    private final static Logger logger = LoggerFactory.getLogger(TokenBlacklist.class);
    private final JwtService jwtService;
    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupService;

    public TokenBlacklist(JwtService jwtService) {
        this.jwtService = jwtService;
        this.cleanupService = Executors.newSingleThreadScheduledExecutor();

        this.cleanupService.scheduleAtFixedRate(
                this::cleanupExpiredTokens,
                10,
                30,
                TimeUnit.MINUTES
        );
    }

    public void addToBlacklist(String token) {
        try {
            long expirationTime = jwtService.getExpirationTimeInMillis(token);
            blacklistedTokens.put(jwtService.extractJti(token), expirationTime);
            logger.debug("Token blacklist'e eklendi: {}", maskToken(token));
        } catch (Exception e) {
            logger.error("Token blacklist'e eklenirken hata: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            String jti = jwtService.extractJti(token);
            Long expirationTime = blacklistedTokens.get(jti);

            if (expirationTime == null) {
                return false;
            }

            if (expirationTime < System.currentTimeMillis()) {
                blacklistedTokens.remove(jti);
                return false;
            }

            return true;
        } catch (Exception e) {
            logger.warn("Token blacklist kontrolü hatası: {}", e.getMessage());
            return false;
        }
    }

    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, Long>> iterator = blacklistedTokens.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (entry.getValue() < now) {
                iterator.remove();
            }
        }

        logger.debug("Blacklist temizleme tamamlandı, mevcut boyut: {}", blacklistedTokens.size());
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 5) + "..." + token.substring(token.length() - 5);
    }

    @PreDestroy
    public void shutdown() {
        cleanupService.shutdown();
    }

}