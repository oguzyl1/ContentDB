package com.contentdb.authentication_service.controller;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.dto.UserListDto;
import com.contentdb.authentication_service.request.UpdateUserRequest;
import com.contentdb.authentication_service.request.UpdateUserRolesRequest;
import com.contentdb.authentication_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@NotEmpty @RequestParam String userId) {
        logger.info("GET /admin/user - Kullanıcı bilgileri getiriliyor");
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/user/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserListDto>> getAllUsers() {
        logger.info("GET /admin/user/all - Tüm soft delete edilmemiş kullanıcılar getiriliyor");
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/user/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserListDto>> getAllInactiveUsers(@NotEmpty @RequestParam int day) {
        logger.info("GET /admin/all - Girilen gün'den beri girmeyen kullanıcılar listeleniyor");
        return ResponseEntity.ok(userService.getInactiveUsers(day));
    }


    @PutMapping("/update-user/information")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserInformation(@RequestParam @NotEmpty String userId, @RequestBody @NotEmpty UpdateUserRequest request) {
        logger.info("PUT /admin/update-user/information - Kullanıcının bilgileri admin tarafından güncelleniyor");
        Object result = userService.updateUserInformation(userId, request, true);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update-user/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserRole(@NotEmpty @RequestParam String userId, @RequestBody @NotEmpty UpdateUserRolesRequest request) {
        logger.info("PUT /admin/update-user/role - Kullanıcının rolü değiştiriliyor");
        return ResponseEntity.ok(userService.updateUserRoles(userId, request));
    }

    @PutMapping("/user/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto toggleAccountLock(@NotEmpty @RequestParam String id, @NotEmpty @RequestParam boolean locked) {
        logger.info("PUT /admin/user/lock - Kullanıcının hesabı kitleniyor");
        return userService.toggleAccountLock(id, locked);
    }

    @PutMapping("/user/failed-attempts/increment")
    @PreAuthorize("hasRole('ADMIN')")
    public void incrementFailedAttempts(@NotEmpty @RequestParam String username) {
        logger.info("PUT /admin//user/failed-attempts/increment - Kullanıcının başarısız giriş sayısı arttırılıyor");
        userService.incrementFailedAttempts(username);
    }


    @PutMapping("/user/failed-attempts/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public void resetFailedAttempts(@NotEmpty @RequestParam String username) {
        logger.info("PUT /admin//user/failed-attempts/reset - Kullanıcının başarısız giriş sayısı sıfırlanıyor");
        userService.resetFailedAttempts(username);
    }

    @GetMapping("/user/locked")
    @PreAuthorize("hasRole('ADMIN')")
    public boolean isAccountLocked(@NotEmpty @RequestParam String username) {
        logger.info("PUT /admin/user/locked - Kullanıcın hesabı kilitli mi diye bakar true false döner");
        return userService.isAccountLocked(username);
    }


}
