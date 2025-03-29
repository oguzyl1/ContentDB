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
    public ResponseEntity<TokenResponse> login(@RequestBody @NotEmpty LoginRequest loginRequest , HttpServletResponse response) {
        logger.info("POST /auth/login - Kullanıcı giriş yapıyor: {}", loginRequest.username());
        return ResponseEntity.ok(userService.login(loginRequest,response));
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


//    /**
//     * ============================
//     * PUBLIC API'LER (Herkese Açık)
//     * ============================
//     */
//
//    @GetMapping("/auth/welcome")
//    public String welcome() {
//        logger.info("Hoş geldiniz mesajı başarıyla verildi.");
//        return "Welcome";
//    }
//
//    @PostMapping("/auth/register")
//    public ResponseEntity<UserDto> register(@RequestBody CreateUserRequest createUserRequest) {
//        logger.info("POST /auth/register - Yeni kullanıcı kaydı başlatılıyor: {}", createUserRequest.username());
//        return ResponseEntity.ok(userService.createUser(createUserRequest));
//    }
//
//    @PostMapping("/auth/login")
//    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
//        logger.info("POST /auth/login - Kullanıcı giriş yapıyor: {}", loginRequest.username());
//        return ResponseEntity.ok(userService.login(loginRequest));
//    }
//
//    @PostMapping("/auth/token/refresh")
//    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
//        logger.info("POST /auth/token/refresh - Kullanıcı için refresh token üretiliyor");
//        String accessToken = authorizationHeader.replace("Bearer ", "");
//
//        String refreshToken = userService.createRefreshToken(accessToken);
//
//        return ResponseEntity.ok(new TokenResponse(null, refreshToken));
//    }
//
//    @PostMapping("/auth/token/create-access")
//    public ResponseEntity<TokenResponse> createRefreshAccessToken(@RequestBody RefreshToAccessTokenRequest request) {
//        logger.info("POST /auth/token/create-access - Refresh token ile access token oluşturuluyor");
//        return ResponseEntity.ok(userService.refreshAccessToken(request.refreshToken()));
//    }
//
//    @PostMapping("/auth/password/reset/initiate")
//    public ResponseEntity<Void> initiatePasswordReset(@RequestBody InitiatePasswordResetRequest request) {
//        logger.info("POST /auth/password/reset/initiate - Şifre sıfırlama isteği gönderiliyor");
//        userService.initiatePasswordReset(request);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping("/auth/password/reset/complete")
//    public ResponseEntity<Void> completePasswordReset(@RequestParam("resetToken") String resetToken,
//                                                      @RequestBody ResetPasswordRequest request) {
//        logger.info("POST /auth/password/reset/complete - Şifre sıfırlama tamamlanıyor");
//        userService.completePasswordReset(resetToken, request);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * ============================
//     * KULLANICI API'LERİ
//     * ============================
//     */
//
//    @PostMapping("/users/logout")
//    public ResponseEntity<Void> logout() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        logger.info("POST /users/logout - Kullanıcı çıkış yapıyor: {}", username);
//        userService.logout(username);
//        return ResponseEntity.ok().build();
//    }
//
//    @PutMapping("/users/update")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserRequest updateUserRequest,
//                                              Authentication authentication) {
//        String username = authentication.getName();
//        logger.info("PUT /users/update - Kullanıcı bilgileri güncelleniyor: {}", username);
//        return ResponseEntity.ok(userService.updateUser(username, updateUserRequest));
//    }
//
//    @PutMapping("/admin/users/{username}/update")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserDto> updateUserByAdmin(@PathVariable String username,
//                                                     @RequestBody UpdateUserRequest updateUserRequest) {
//        logger.info("PUT /admin/users/{}/update - Admin tarafından kullanıcı bilgileri güncelleniyor", username);
//        return ResponseEntity.ok(userService.updateUser(username, updateUserRequest));
//    }
//
//
//    @DeleteMapping("/users/{username}/delete")
//    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
//    public ResponseEntity<Void> softDeleteUser(@PathVariable String username) {
//        logger.info("DELETE /users/{}/delete - Kullanıcı soft delete işlemi başlatılıyor", username);
//        userService.softDeleteUser(username);
//        return ResponseEntity.ok().build();
//    }
//
//
//    @PutMapping("/users/password/change")
//    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        logger.info("PUT /users/password/change - Kullanıcı şifresini değiştiriyor");
//        userService.changePassword(username, changePasswordRequest);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * ============================
//     * ADMIN API'LERİ (Yetki Gerektirir)
//     * ============================
//     */
//
//    @GetMapping("/users/active-users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<UserListDto>> getAllActiveUsers() {
//        logger.info("GET /users/active-users - Aktif kullanıcı listesi getiriliyor");
//        return ResponseEntity.ok(userService.getAllUsers());
//    }
//
//
//    @PutMapping("/users/{username}/roles")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserDto> updateUserRoles(@PathVariable String username,
//                                                   @RequestBody UpdateUserRolesRequest request) {
//        logger.info("PUT /users/{}/roles - Kullanıcının rolü değiştiriliyor", username);
//        return ResponseEntity.ok(userService.updateUserRoles(username, request));
//    }
//
//    @PutMapping("/users/ban-user")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserDto> banUser(@RequestParam String username, @RequestParam boolean locked) {
//        logger.info("PUT /users/ban-user - Kullanıcı banlanıyor: {}", username);
//        return ResponseEntity.ok(userService.toggleAccountLock(username, locked));
//    }
//
//    @GetMapping("/users/inactive-users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<UserListDto>> getAllInactiveUsers(@RequestParam int day) {
//        logger.info("GET /users/inactive-users - İnaktif kullanıcılar getiriliyor");
//        return ResponseEntity.ok(userService.getInactiveUsers(day));
//    }
}
