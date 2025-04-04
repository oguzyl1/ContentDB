package com.contentdb.comment_service.exception;

import org.springframework.http.HttpStatus;

public class EmptyException extends BaseException {

    public EmptyException(String message) {
        super(message, "EMPTY", HttpStatus.NOT_FOUND);
    }
}
