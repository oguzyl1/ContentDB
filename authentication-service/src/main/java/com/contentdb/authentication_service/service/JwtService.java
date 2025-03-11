package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.exception.UnauthorizedAccessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt_private_key}")
    private String SECRET;
    private static final long ACCESS_TOKEN_VALIDITY = 15;
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60;
    private static final long RESET_TOKEN_VALIDITY = 24 * 60;


    // ========================
    // Token Oluşturma Metotları
    // ========================

    public String generateToken(String username, String userId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);
        claims.put("type", "access");
        return createToken(claims, username, ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(String username, String userId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);
        claims.put("type", "refresh");
        return createToken(claims, username, REFRESH_TOKEN_VALIDITY);
    }

    public String generateResetToken(String username, String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("type", "reset");
        return createToken(claims, username, RESET_TOKEN_VALIDITY);
    }


    // ========================
    // Token Doğrulama Metotları
    // ========================

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUser(token);
        Instant expirationInstant = extractExpiration(token).toInstant();
        return userDetails.getUsername().equals(username) && expirationInstant.isAfter(Instant.now());
    }

    public Boolean validateRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        return "refresh".equals(claims.get("type", String.class)) &&
                claims.getExpiration().after(new Date());
    }

    public Boolean validateResetToken(String token) {
        Claims claims = extractAllClaims(token);
        return "reset".equals(claims.get("type", String.class)) &&
                claims.getExpiration().after(new Date());
    }

    public void validateAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Claims claims = extractAllClaims(token);
                String tokenType = claims.get("type", String.class);
                if (!"access".equals(tokenType)) {
                    throw new UnauthorizedAccessException();
                }
            }
        }
    }

    // ========================
    // Token İçeriği Çekme Metotları
    // ========================

    public String extractUser(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    public List<String> extractRoles(String token) {
        return extractAllClaims(token).get("roles", List.class);
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }


    // ========================
    // Yardımcı Metotlar
    // ========================

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String createToken(Map<String, Object> claims, String username, long validityMinutes) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(validityMinutes, ChronoUnit.MINUTES)))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}
