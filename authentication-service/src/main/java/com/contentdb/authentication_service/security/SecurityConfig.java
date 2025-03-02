package com.contentdb.authentication_service.security;

import com.contentdb.authentication_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
            "/api/v1/auth/welcome",
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/password/reset/initiate",
            "/api/v1/auth/password/reset/complete"
    };

    private static final String[] ADMIN_URLS = {
            "/api/v1/users/active-users",
            "/api/v1/users/inactive-users",
            "/api/v1/users/ban-user"
    };

    private final JwtAuthFilter jwtAuthFilter;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          @Lazy UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(PUBLIC_URLS).permitAll()

                        // Admin only endpoints
                        .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/{userId}").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/{username}/delete").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/{username}/roles").hasRole("ADMIN")

                        // User specific endpoints - require authentication
                        .requestMatchers("/api/v1/users/logout").authenticated()
                        .requestMatchers("/api/v1/users/password/change").authenticated()
                        .requestMatchers("/api/v1/auth/token/create-access").authenticated()
                        .requestMatchers("/api/v1/auth/token/refresh").authenticated()  // Refresh token endpoint authentication required

                        // The update endpoint is handled by @PreAuthorize in the controller
                        // This ensures only the user or an admin can update a specific user
                        .requestMatchers("/api/v1/users/{username}/update").authenticated()
                        .requestMatchers("api/v1/users/{username}/delete").authenticated()

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
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
