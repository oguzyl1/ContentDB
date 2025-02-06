package com.contentdb.authentication_service.request;

import com.contentdb.authentication_service.model.Role;

import java.util.Set;

public record CreateUserRequest(

        String name,
        String lastName,
        String email,
        String username,
        String password,
        Set<Role> authorities
) {

    private static class CreateUserRequestBuilder {
        private String name;
        private String lastName;
        private String email;
        private String username;
        private String password;
        private Set<Role> authorities;

        public CreateUserRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CreateUserRequestBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public CreateUserRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public CreateUserRequestBuilder username(String username) {
            this.username = username;
            return this;
        }

        public CreateUserRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public CreateUserRequestBuilder authorities(Set<Role> authorities) {
            this.authorities = authorities;
            return this;
        }

        public CreateUserRequest build() {
            return new CreateUserRequest(
                    name,
                    lastName,
                    email,
                    username,
                    password,
                    authorities);
        }
    }
}
