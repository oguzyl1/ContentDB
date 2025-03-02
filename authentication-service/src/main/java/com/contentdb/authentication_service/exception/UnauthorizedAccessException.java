package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedAccessException extends BaseException {

    public UnauthorizedAccessException() {
        super("Rol değişikliği yapmak için yetki yok.", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
