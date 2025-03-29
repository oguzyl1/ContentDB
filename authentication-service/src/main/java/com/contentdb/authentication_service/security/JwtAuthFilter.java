package com.contentdb.authentication_service.security;

import com.contentdb.authentication_service.exception.InvalidTokenException;
import com.contentdb.authentication_service.exception.UnauthorizedAccessException;
import com.contentdb.authentication_service.service.JwtService;
import com.contentdb.authentication_service.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final TokenBlacklist tokenBlacklist;

    public JwtAuthFilter(JwtService jwtService, @Lazy UserService userService, TokenBlacklist tokenBlacklist) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/register") ||
                path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/password-reset")||
                path.startsWith("/api/auth/password-reset/complete");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // Header'dan token al
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                username = jwtService.extractUser(token);

                if (tokenBlacklist.isBlacklisted(token)) {
                    throw new UnauthorizedAccessException("Geçersiz veya süresi dolmuş token.");
                }
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token süresi dolmuş. Lütfen tekrar giriş yapın.");
                return;
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Geçersiz token.");
                return;
            }
        }

        // Cookie'den token al
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("access_token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        try {
                            username = jwtService.extractUser(token);

                            if (tokenBlacklist.isBlacklisted(token)) {
                                throw new InvalidTokenException("Geçersiz veya süresi dolmuş token.");
                            }
                        } catch (ExpiredJwtException e) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Token süresi dolmuş. Lütfen tekrar giriş yapın.");
                            return;
                        } catch (Exception e) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Geçersiz token.");
                            return;
                        }
                        break;
                    }
                }
            }
        }

        // Authentication kontrolü ve filtreleme
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails user = userService.loadUserByUsername(username);
            if (jwtService.validateToken(token, user)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}