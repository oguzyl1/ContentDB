package com.contentdb.authentication_service.controller;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.request.*;
import com.contentdb.authentication_service.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;

@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @NotEmpty CreateUserRequest createUserRequest) {
        logger.info("POST /auth/register - Kullanıcı kayıt işlemi başlatılıyor: {}", createUserRequest.username());
        return ResponseEntity.ok(userService.createUser(createUserRequest));

    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @NotEmpty LoginRequest loginRequest, HttpServletResponse response) {
        logger.info("POST /auth/login - Kullanıcı giriş yapıyor: {}", loginRequest.username());
        return ResponseEntity.ok(userService.login(loginRequest, response));
    }

    @PostMapping("/password-reset/initiate")
    public ResponseEntity<Void> initiateReset(@RequestBody @NotEmpty InitiatePasswordResetRequest request) {
        logger.info("POST /auth/password/initiate-reset - Kullanıcının şifre sıfırlama isteği gönderiliyor");
        userService.initiatePasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-reset/complete")
    public ResponseEntity<Void> completeReset(@NotEmpty @RequestParam String token, @RequestBody @NotEmpty ResetPasswordRequest request) {
        logger.info("POST /auth/password/complete-reset - Kullanıcının şifre sıfırlama işlemi tamamlanıyor");
        userService.completePasswordReset(token, request);
        return ResponseEntity.ok().build();
    }

}