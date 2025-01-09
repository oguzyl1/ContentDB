package com.contentdb.authentication_service.controller;


import com.contentdb.authentication_service.dto.*;
import com.contentdb.authentication_service.service.JwtService;
import com.contentdb.authentication_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome";
    }

    @PostMapping("createUser")
    public ResponseEntity<UserDto> createUser (@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }

    @PostMapping("generateToken")
    public ResponseEntity<String> generateToken(@RequestBody CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.generateToken(createUserRequest));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<UserListDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/updateUser/{username}")
    public ResponseEntity<UserDto> updateUser (@PathVariable(value = "username") String username,@RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(userService.updateUser(username,updateUserRequest));
    }

    @PutMapping("/changePassword/{username}")
    public ResponseEntity<Void> changePassword(@PathVariable(value = "username") String username , @RequestBody PasswordResetRequest passwordResetRequest) {
        userService.changePassword(username,passwordResetRequest);
        return ResponseEntity.ok().build();
    }





}
