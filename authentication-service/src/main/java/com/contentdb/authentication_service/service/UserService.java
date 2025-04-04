package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.dto.UserListDto;
import com.contentdb.authentication_service.exception.UserNotFoundException;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.UserRepository;
import com.contentdb.authentication_service.request.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    private final AuthenticationService authenticationService;
    private final UserManagementService userManagementService;
    private final PasswordService passwordService;
    private final AccountLockService accountLockService;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;

    public UserService(AuthenticationService authenticationService, UserManagementService userManagementService, PasswordService passwordService, AccountLockService accountLockService, UserQueryService userQueryService,UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userManagementService = userManagementService;
        this.passwordService = passwordService;
        this.accountLockService = accountLockService;
        this.userQueryService = userQueryService;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new UserNotFoundException(username));
    }

    public UserDto getUserById(String id) {
        return userQueryService.getUserById(id);
    }

    public TokenResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        return authenticationService.login(loginRequest,response);
    }

    public Object updateUserInformation(String id, UpdateUserRequest request, boolean isAdmin) {
        return userManagementService.updateUser(id, request, isAdmin);
    }

    public UserDto createUser(CreateUserRequest request) {
        return userManagementService.createUser(request);
    }

    public List<UserListDto> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    public void softDeleteUser(String id) {
        userManagementService.softDeleteUser(id);
    }

    public void changePassword(ChangePasswordRequest request) {
        passwordService.changePassword(request);
    }

    public TokenResponse refreshAccessToken(UserDetails user) {
        return authenticationService.refreshAccessToken(user);
    }

    public void initiatePasswordReset(InitiatePasswordResetRequest request) {
        authenticationService.initiatePasswordReset(request);
    }

    public void completePasswordReset(String token, ResetPasswordRequest request) {
        authenticationService.completePasswordReset(token, request);
    }

    public UserDto updateUserRoles(String id, UpdateUserRolesRequest request) {
        return userManagementService.updateUserRoles(id, request);
    }

    public UserDto toggleAccountLock(String id, boolean locked) {
        return accountLockService.toggleAccountLock(id, locked);
    }

    public List<UserListDto> getInactiveUsers(int day) {
        return userManagementService.getInactiveUsers(day);
    }

    public void incrementFailedAttempts(String username) {
        accountLockService.incrementFailedAttempts(username);
    }

    public void resetFailedAttempts(String username) {
        accountLockService.resetFailedAttempts(username);
    }

    public void lockAccount(String username) {
        accountLockService.lockAccount(username);
    }

    public boolean isAccountLocked(String username) {
        return accountLockService.isAccountLocked(username);
    }


}
