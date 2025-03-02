package com.contentdb.authentication_service.exception;


import org.springframework.http.HttpStatus;

public class PasswordIsNotSameException extends BaseException {
    public PasswordIsNotSameException() {
        super("Şifreler eşleşmiyor. Lütfen Kontrol Edin.", "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }
}
