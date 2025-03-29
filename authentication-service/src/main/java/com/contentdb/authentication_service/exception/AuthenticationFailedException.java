package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends BaseException {

    public AuthenticationFailedException(String message) {
        super(message, "UNAUTHORİZED", HttpStatus.UNAUTHORIZED);
    }
}
