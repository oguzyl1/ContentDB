package com.contentdb.authentication_service.controller;


import com.contentdb.authentication_service.dto.*;
import com.contentdb.authentication_service.request.*;
import com.contentdb.authentication_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/auth/welcome")
    public String welcome() {
        return "Welcome";
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserDto> register(@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
    }

    @PostMapping("/auth/token/reset")
    public ResponseEntity<String> resetToken(@RequestBody ResetTokenRequest resetTokenRequest) {
        return ResponseEntity.ok(userService.resetToken(resetTokenRequest));
    }

    @PostMapping("/auth/token/refresh")
    public ResponseEntity<String> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(userService.createRefreshToken(refreshTokenRequest.username()));
    }

    @PostMapping("/auth/password-reset/initiate")
    public ResponseEntity<Void> initiatePasswordReset(@RequestBody InitiatePasswordResetRequest initiateRequest) {
        userService.initiatePasswordReset(initiateRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth/password-reset/complete")
    public ResponseEntity<Void> completePasswordReset(@RequestBody CompletePasswordResetRequest resetRequest) {
        userService.completePasswordReset(resetRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserListDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/users/{username}/update")
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUser(@PathVariable(value = "username") String username,
                                              @RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(userService.updateUser(username, updateUserRequest));
    }

    @PutMapping("/users/{username}/password")
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<Void> changePassword(@PathVariable(value = "username") String username,
                                               @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(username, changePasswordRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{username}/delete")
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<Void> softDeleteUser(@PathVariable("username") String username) {
        userService.softDeleteUser(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{username}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserRoles(@PathVariable("username") String username,
                                                   @RequestBody UpdateUserRolesRequest updateRolesRequest) {
        return ResponseEntity.ok(userService.updateUserRoles(username, updateRolesRequest));
    }
}