package com.contentdb.authentication_service.controller;

import com.contentdb.authentication_service.dto.*;
import com.contentdb.authentication_service.request.*;
import com.contentdb.authentication_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * ============================
     * PUBLIC API'LER (Herkese Açık)
     * ============================
     */

    @GetMapping("/auth/welcome")
    public String welcome() {
        logger.info("Hoş geldiniz mesajı başarıyla verildi.");
        return "Welcome";
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@RequestBody CreateUserRequest createUserRequest) {
        logger.info("POST /auth/register - Yeni kullanıcı kaydı başlatılıyor: {}", createUserRequest.username());
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        logger.info("POST /auth/login - Kullanıcı giriş yapıyor: {}", loginRequest.username());
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        logger.info("POST /auth/token/refresh - Kullanıcı için refresh token üretiliyor");
        String accessToken = authorizationHeader.replace("Bearer ", "");

        String refreshToken = userService.createRefreshToken(accessToken);

        return ResponseEntity.ok(new TokenResponse(null, refreshToken));
    }

    @PostMapping("/auth/token/create-access")
    public ResponseEntity<TokenResponse> createRefreshAccessToken(@RequestBody RefreshToAccessTokenRequest request) {
        logger.info("POST /auth/token/create-access - Refresh token ile access token oluşturuluyor");
        return ResponseEntity.ok(userService.refreshAccessToken(request.refreshToken()));
    }

    @PostMapping("/auth/password/reset/initiate")
    public ResponseEntity<Void> initiatePasswordReset(@RequestBody InitiatePasswordResetRequest request) {
        logger.info("POST /auth/password/reset/initiate - Şifre sıfırlama isteği gönderiliyor");
        userService.initiatePasswordReset(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/password/reset/complete")
    public ResponseEntity<Void> completePasswordReset(@RequestParam("resetToken") String resetToken,
                                                      @RequestBody ResetPasswordRequest request) {
        logger.info("POST /auth/password/reset/complete - Şifre sıfırlama tamamlanıyor");
        userService.completePasswordReset(resetToken, request);
        return ResponseEntity.ok().build();
    }

    /**
     * ============================
     * KULLANICI API'LERİ
     * ============================
     */

    @PostMapping("/users/logout")
    public ResponseEntity<Void> logout() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("POST /users/logout - Kullanıcı çıkış yapıyor: {}", username);
        userService.logout(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{username}/update")
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable String username,
                                              @RequestBody UpdateUserRequest updateUserRequest) {
        logger.info("PUT /users/{}/update - Kullanıcı bilgileri güncelleniyor", username);
        return ResponseEntity.ok(userService.updateUser(username, updateUserRequest));
    }

    @DeleteMapping("/users/{username}/delete")
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteUser(@PathVariable String username) {
        logger.info("DELETE /users/{}/delete - Kullanıcı soft delete işlemi başlatılıyor", username);
        userService.softDeleteUser(username);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/users/password/change")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("PUT /users/password/change - Kullanıcı şifresini değiştiriyor");
        userService.changePassword(username, changePasswordRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * ============================
     * ADMIN API'LERİ (Yetki Gerektirir)
     * ============================
     */

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        logger.info("GET /users/{} - Kullanıcı bilgileri getiriliyor", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/users/active-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserListDto>> getAllActiveUsers() {
        logger.info("GET /users/active-users - Aktif kullanıcı listesi getiriliyor");
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @PutMapping("/users/{username}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserRoles(@PathVariable String username,
                                                   @RequestBody UpdateUserRolesRequest request) {
        logger.info("PUT /users/{}/roles - Kullanıcının rolü değiştiriliyor", username);
        return ResponseEntity.ok(userService.updateUserRoles(username, request));
    }

    @PutMapping("/users/ban-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> banUser(@RequestParam String username, @RequestParam boolean locked) {
        logger.info("PUT /users/ban-user - Kullanıcı banlanıyor: {}", username);
        return ResponseEntity.ok(userService.toggleAccountLock(username, locked));
    }

    @GetMapping("/users/inactive-users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserListDto>> getAllInactiveUsers(@RequestParam int day) {
        logger.info("GET /users/inactive-users - İnaktif kullanıcılar getiriliyor");
        return ResponseEntity.ok(userService.getInactiveUsers(day));
    }
}
