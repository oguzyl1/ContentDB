package com.contentdb.authentication_service.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record PasswordResetRequest(
        @NotBlank(message = "Eski şifre boş olamaz!")
        @Size(min = 8, max = 25 ,message = "Şifre en az 8 karakter olmalıdır")
        String oldPassword,

        @NotBlank(message = "Yeni şifre boş olamaz!")
        @Size(min = 8, max = 25 ,message = "Şifre en az 8 karakter olmalıdır")
        String newPassword
) {
}
