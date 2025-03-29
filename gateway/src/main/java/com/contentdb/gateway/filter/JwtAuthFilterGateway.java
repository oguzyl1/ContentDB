package com.contentdb.gateway.filter;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.security.Key;


@Component
public class JwtAuthFilterGateway implements WebFilter {

    @Value("${jwt_private_key}")
    private String SECRET;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilterGateway.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String token = null;

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        // Eğer header'da yoksa, cookie'lerden kontrol et
        if (token == null) {
            String cookies = request.getHeaders().getFirst("Cookie");
            if (cookies != null) {
                String[] cookieParts = cookies.split(";");
                for (String cookie : cookieParts) {
                    cookie = cookie.trim();
                    if (cookie.startsWith("access_token=")) {
                        token = cookie.substring("access_token=".length());
                        break;
                    }
                }
            }
        }

        logger.info("JWT Token: {}", token);

        if (token != null) {
            try {
                Claims claims = Jwts.parser()
                        .verifyWith(getSignKey())
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                String userId = (String) claims.get("userId");
                logger.info("JWT'den çözümlenen userId: {}", userId);

                if (userId != null) {
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-User-Id", userId)
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                }
            } catch (Exception e) {
                logger.error("JWT çözümleme hatası: ", e);
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return Mono.empty();
            }
        } else {
            logger.warn("Token bulunamadı, kullanıcı kimliği belirlenemedi.");
        }

        return chain.filter(exchange);
    }


    private SecretKey getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
}