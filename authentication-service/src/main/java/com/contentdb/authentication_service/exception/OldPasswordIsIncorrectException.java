package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class OldPasswordIsIncorrectException extends BaseException {

    public OldPasswordIsIncorrectException() {
            super("Eski şifre hatalı. Lütfen şifreyi kontrol edin.", "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }
}
