package com.contentdb.authentication_service.controller;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.request.ChangePasswordRequest;
import com.contentdb.authentication_service.request.TokenResponse;
import com.contentdb.authentication_service.request.UpdateUserRequest;
import com.contentdb.authentication_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;

@Validated
@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);


    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/my-account/profile")
    public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        logger.info("GET /users/my-account/profile- Kullanıcı bilgileri getiriliyor: {}", userDetails.getUsername());
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }

    @PutMapping("/my-account/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMyself(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @NotEmpty UpdateUserRequest updateUserRequest) {
        User user = (User) userDetails;
        logger.info("PUT /users/my-account/update - Kullanıcı kendi bilgilerini güncelliyor.");
        Object result = userService.updateUserInformation(user.getId(), updateUserRequest, false);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/my-account/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal UserDetails userDetails) {
        User user = (User) userDetails;
        logger.info("PUT /users/my-account/delete - Kullanıcı kendi hesabını siliyor");
        userService.softDeleteUser(user.getId());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/my-account/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changePassword(@RequestBody @NotEmpty ChangePasswordRequest request) {
        logger.info("PUT /users/my-account/change-password - Kullanıcı şifresini değiştiriyor.");
        userService.changePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/create-access")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TokenResponse> refreshAccessToken(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("POST /users/create-access - Kullanıcı için access token oluşturuluyor");
        return ResponseEntity.ok(userService.refreshAccessToken(userDetails));
    }


//    @GetMapping("/logout")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
//        User user = (User) userDetails;
//        logger.info("GET /users/logout - Kullanıcı çıkış yapıyor: {}", userDetails.getUsername());
//        userService.logout(user.getId());
//        SecurityContextHolder.clearContext();
//        logger.info("Çıkış işlemi başarılı");
//        return ResponseEntity.ok().build();
//    }

}
