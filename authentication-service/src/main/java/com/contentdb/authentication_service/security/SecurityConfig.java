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
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
            "/api/v1/auth/register/**",         //+
            "/api/v1/auth/login/**",            //+
            "/api/v1/auth/token/reset/**",      //
            "/api/v1/auth/token/refresh/**",
            "/api/v1/auth/password-reset/initiate",
            "/api/v1/auth/password-reset/complete"
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
                        .requestMatchers(PUBLIC_URLS).permitAll())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/users").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/{username}/roles/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/{username}/**")
                        .access((authentication, context) -> {
                            logger.info("Authorization check started!");

                            String requestedUsername = context.getVariables().get("username");
                            Authentication auth2 = authentication.get();

                            logger.info("Authentication Object: {}", auth2);
                            logger.info("Authentication Principal: {}", auth2.getPrincipal());

                            if (!(auth2.getPrincipal() instanceof UserDetails)) {
                                logger.info("Principal is not UserDetails, access denied!");
                                return new AuthorizationDecision(false);
                            }

                            String currentUsername = ((UserDetails) auth2.getPrincipal()).getUsername();

                            boolean isAdmin = auth2.getAuthorities().stream()
                                    .anyMatch(a -> a.toString().equals("ROLE_ADMIN"));

                            logger.info("Requested Username: {}", requestedUsername);
                            logger.info("Current Username: {}", currentUsername);
                            logger.info("Is Admin: {}", isAdmin);

                            return (requestedUsername.equals(currentUsername) || isAdmin)
                                    ? new AuthorizationDecision(true)
                                    : new AuthorizationDecision(false);
                        })
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
