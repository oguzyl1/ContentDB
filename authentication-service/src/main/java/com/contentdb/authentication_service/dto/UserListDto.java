package com.contentdb.authentication_service.dto;

import com.contentdb.authentication_service.model.Role;
import com.contentdb.authentication_service.model.User;

import java.util.Set;

public class UserListDto {

    private String id;
    private String name;
    private String lastName;
    private String email;
    private String username;
    private boolean accountNonExpired;
    private boolean isEnabled;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private Set<Role> authorities;

    public static UserListDto converToUserListDto(User user) {
        return new UserListDto(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.isAccountNonExpired(),
                user.isEnabled(),
                user.isAccountNonLocked(),
                user.isCredentialsNonExpired(),
                user.getAuthorities()
        );
    }

    public UserListDto(String id,
                       String name,
                       String lastName,
                       String email,
                       String username,
                       boolean accountNonExpired,
                       boolean isEnabled,
                       boolean accountNonLocked,
                       boolean credentialsNonExpired,
                       Set<Role> authorities) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.accountNonExpired = accountNonExpired;
        this.isEnabled = isEnabled;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.authorities = authorities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public Set<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }
}
