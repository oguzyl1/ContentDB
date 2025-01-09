package com.contentdb.authentication_service.dto;

import com.contentdb.authentication_service.model.Role;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

public class UpdateUserRequest {
        @NotBlank(message = "İsim boş olamaz!")
        @Size(min = 2, max = 15, message = "isim 2 ila 15 karakter arasında olmalıdır!")
        String name;

        @NotBlank(message = "Soyisim boş olamaz!")
        @Size(min = 2, max = 50, message = "Soyisim 2 ile 50 karakter arasında olmalıdır")
        String lastName;

        @NotBlank(message = "Kullanıcı adı boş olamaz")
        @Size(min = 4, max = 20, message = "Kullanıcı adı 4 ile 20 karakter arasında olmalıdır")
        String username;

        @NotBlank(message = "Yetkiler boş olamaz!")
        Set<Role> authorities;

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

        public UpdateUserRequest(String name, String lastName, String username, Set<Role> authorities) {
                this.name = name;
                this.lastName = lastName;
                this.username = username;
                this.authorities = authorities;
        }

        public UpdateUserRequest() {
        }
}
