package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class PasswordsCannotBeTheSameException extends BaseException{
    public PasswordsCannotBeTheSameException() {
        super("Yeni şifre eski şifre ile aynı olamaz. Lütfen farklı bir şifre girin.", "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }
}
