package com.contentdb.authentication_service.security;

import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.RefreshTokenRepository;
import com.contentdb.authentication_service.service.JwtService;
import com.contentdb.authentication_service.service.RefreshTokenService;
import com.contentdb.authentication_service.service.UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/password-reset/initiate",
            "/api/auth/password-reset/complete"
    };

    private static final String[] ADMIN_URLS = {
            "/api/admin/**"
    };

    private static final String[] AUTHENTICATED_URLS = {
            "/api/users/**"
    };

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklist tokenBlacklist;
    private final RefreshTokenService refreshTokenService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, JwtService jwtService,
                          @Lazy UserService userService,
                          PasswordEncoder passwordEncoder, TokenBlacklist tokenBlacklist,
                          RefreshTokenRepository refreshTokenRepository, RefreshTokenService refreshTokenService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklist = tokenBlacklist;
        this.refreshTokenService = refreshTokenService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOriginPattern("*"); // Tüm origin'leri kabul et
                    config.addAllowedMethod("*"); // Tüm HTTP metodlarına izin ver
                    config.addAllowedHeader("*"); // Tüm header'ları kabul et
                    config.setAllowCredentials(true);
                    return config;
                })).logout(this::configureLogout)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                        .requestMatchers(AUTHENTICATED_URLS).authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
        logout
                .logoutUrl("/api/users/logout")
                .addLogoutHandler((request, response, authentication) -> {
                    Cookie[] cookies = request.getCookies();
                    String accessToken = null;
                    if (cookies != null) {
                        for (Cookie cookie : cookies) {
                            if ("access_token".equals(cookie.getName())) {
                                accessToken = cookie.getValue();
                                break;
                            }
                        }
                    }

                    if (accessToken != null) {
                        // Tokenı blacklist'e ekle
                        tokenBlacklist.addToBlacklist(accessToken);

                        // Kullanıcı kimliğini al (örneğin, JWT'den)
                        String userId = jwtService.extractUserId(accessToken);

                        if (userId != null) {
                            // Refresh tokenı sil
                            refreshTokenService.deleteRefreshTokenByUserId(userId);
                        }

                        // Cookieyi temizle
                        Cookie accessTokenCookie = new Cookie("access_token", null);
                        accessTokenCookie.setHttpOnly(true);
                        accessTokenCookie.setSecure(true);
                        accessTokenCookie.setMaxAge(0); // Cookieyi sil
                        accessTokenCookie.setPath("/");
                        response.addCookie(accessTokenCookie);
                    }
                })
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
