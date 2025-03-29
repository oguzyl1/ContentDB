package com.contentdb.authentication_service.exception;


import org.springframework.http.HttpStatus;

public class PasswordIsNotSameException extends BaseException {
    public PasswordIsNotSameException(String message) {
        super(message, "BAD REQUEST", HttpStatus.BAD_REQUEST);
    }
}
