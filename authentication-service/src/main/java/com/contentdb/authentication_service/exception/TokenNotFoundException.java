package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends BaseException {
    public TokenNotFoundException(String message) {
        super(message, "NOT FOUND", HttpStatus.NOT_FOUND);
    }
}
