package com.contentdb.authentication_service.request;

import com.contentdb.authentication_service.model.Role;

import java.util.Set;

public class UpdateUserRequest {

    private String name;
    private String lastName;
    private String username;
    private Set<Role> authorities;
    private String email;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public UpdateUserRequest(String name, String lastName, String username, Set<Role> authorities, String email) {
        this.name = name;
        this.lastName = lastName;
        this.username = username;
        this.authorities = authorities;
        this.email = email;
    }

    public UpdateUserRequest() {
    }
}
