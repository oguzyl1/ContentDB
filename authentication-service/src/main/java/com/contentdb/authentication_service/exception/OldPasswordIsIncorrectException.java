package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class OldPasswordIsIncorrectException extends BaseException {

    public OldPasswordIsIncorrectException(String message) {
            super(message, "BAD REQUEST", HttpStatus.BAD_REQUEST);
    }
}
