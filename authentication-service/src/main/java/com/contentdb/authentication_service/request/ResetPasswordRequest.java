package com.contentdb.authentication_service.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "Yeni şifre boş olamaz!")
        @Size(min = 8, max = 25, message = "Şifre en az 8 karakter olmalıdır")
        String newPassword,

        @NotBlank(message = "Yeni şifre boş olamaz!")
        @Size(min = 8, max = 25, message = "Şifre en az 8 karakter olmalıdır")
        String newPasswordAgain

) {
}
