package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class ThisAccountLockedException extends BaseException {

    public ThisAccountLockedException(String message) {
        super(message, "BAD REQUEST", HttpStatus.BAD_REQUEST);
    }
}
