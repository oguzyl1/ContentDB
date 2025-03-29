package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(String message) {
        super(message, "USERNAME_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
