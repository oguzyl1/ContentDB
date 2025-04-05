package com.contentdb.comment_service.exception;

import org.springframework.http.HttpStatus;

public class UserIdNotSameException extends BaseException {

    public UserIdNotSameException(String message) {
        super(message, "BAD REQUEST", HttpStatus.BAD_REQUEST);
    }
}
