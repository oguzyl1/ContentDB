package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends BaseException {

    public AuthenticationFailedException() {
        super("Kimlik doğrulama işlemi başarısız oldu.", "UNAUTHORİZED", HttpStatus.UNAUTHORIZED);
    }
}
