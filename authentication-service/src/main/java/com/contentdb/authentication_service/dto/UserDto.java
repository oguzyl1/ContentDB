package com.contentdb.authentication_service.dto;

import com.contentdb.authentication_service.model.Role;
import com.contentdb.authentication_service.model.User;

import java.util.Set;

// serileştirmeleri kaldır bir daha dene hata aldın


public class UserDto {

    private String id;
    private String name;
    private String lastName;
    private String email;
    private String username;
    private Set<Role> authorities;

    public static UserDto convertToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getAuthorities()
        );
    }

    public UserDto() {
    }

    public UserDto(String id, String name, String lastName, String email, String username, Set<Role> authorities) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
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

    public Set<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }
}
