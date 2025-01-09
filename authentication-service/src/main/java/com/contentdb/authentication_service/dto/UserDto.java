package com.contentdb.authentication_service.dto;

import com.contentdb.authentication_service.model.Role;
import com.contentdb.authentication_service.model.User;

import java.util.Set;

public class UserDto {

    private String name;
    private String lastName;
    private String email;
    private String username;
    private Set<Role> authorities;

    public static UserDto convertToUserDto(User user) {
        return new UserDto(
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getAuthorities()
        );
    }

    public UserDto() {
    }

    public UserDto(String name, String lastName, String email, String username,Set<Role> authorities) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
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

    public Set<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }
}
