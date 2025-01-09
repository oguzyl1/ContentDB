package com.contentdb.authentication_service.dto;

import com.contentdb.authentication_service.model.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

public record CreateUserRequest(

        @NotBlank(message = "İsim boş olamaz!")
        @Size(min = 2,max = 15,message = "isim 2 ila 15 karakter arasında olmalıdır!")
        String name,

        @NotBlank(message = "Soyisim boş olamaz!")
        @Size(min = 2, max = 50, message = "Soyisim 2 ile 50 karakter arasında olmalıdır")
        String lastName,

        @NotBlank(message = "Email boş olamaz!")
        @Email(message = "Geçerli bir email adresi giriniz")
        String email,

        @NotBlank(message = "Kullanıcı adı boş olamaz")
        @Size(min = 4, max = 20, message = "Kullanıcı adı 4 ile 20 karakter arasında olmalıdır")
        String username,

        @NotBlank(message = "Şifre boş olamaz!")
        @Size(min = 8, max = 25 ,message = "Şifre en az 8 karakter olmalıdır")
        String password,

        @NotBlank(message = "Yetkiler boş olamaz!")
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
