package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;

public class UserIdEmptyException extends BaseException {

    public UserIdEmptyException(String message) {
        super(message, "EMPTY", HttpStatus.NOT_FOUND);
    }
}
