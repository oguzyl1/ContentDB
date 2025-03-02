package com.contentdb.authentication_service.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

    /**
     * Access token oluşturur.
     */
    public String generateToken(String username, String userId, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roles", roles);
        claims.put("username", username);
        claims.put("type", "access");
        return createToken(claims, username, ACCESS_TOKEN_VALIDITY);
    }

    /**
     * Şifre Sıfırlama işlem İçin Token Üretir
     */
    public String generateResetToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("type", "reset");
        return createToken(claims, email, ACCESS_TOKEN_VALIDITY);
    }

    /**
     * Refresh token oluşturur.
     */
    public String generateRefreshToken(String username, String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("type", "refresh");
        return createToken(claims, username, REFRESH_TOKEN_VALIDITY);
    }


    /**
     * Verilen token’ın geçerliliğini kontrol eder.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUser(token);
        Instant expirationInstant = extractExpiration(token).toInstant();
        return userDetails.getUsername().equals(username) && expirationInstant.isAfter(Instant.now());
    }

    /**
     * Şifre sıfırlama token’ının geçerliliğini kontrol eder.
     */
    public Boolean validateResetToken(String token) {
        Claims claims = extractAllClaims(token);
        String type = claims.get("type", String.class);
        Date expiration = claims.getExpiration();
        return "reset".equals(type) && expiration.after(new Date());
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("roles", List.class);
    }


    /**
     * Refresh token’ın geçerliliğini kontrol eder.
     */
    public Boolean validateRefreshToken(String token) {
        Claims claims = extractAllClaims(token);
        String type = claims.get("type", String.class);
        Date expiration = claims.getExpiration();
        return "refresh".equals(type) && expiration.after(new Date());
    }


    /**
     * Token’dan kullanıcı adını (subject) çeker.
     */
    public String extractUser(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }


    /**
     * Token’dan userId bilgisini çeker.
     */
    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", String.class);
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * Token’ın son kullanma tarihini döner.
     */
    private Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    /**
     * Belirtilen claim’ler ve kullanıcı için token üretir.
     */
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

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
