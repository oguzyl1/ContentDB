package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException {

    public InvalidTokenException() {
        super("Geçersiz veya süresi dolmuş", "UNAUTHORİZED", HttpStatus.UNAUTHORIZED);
    }
}
