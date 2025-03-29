package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class PasswordsCannotBeTheSameException extends BaseException{
    public PasswordsCannotBeTheSameException(String message) {
        super(message, "BAD REQUEST", HttpStatus.BAD_REQUEST);
    }
}
