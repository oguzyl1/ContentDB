package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;

public class InvalidRequestException extends BaseException {
    public InvalidRequestException(String message) {
        super(message, "BAD REQUEST", HttpStatus.BAD_REQUEST);
    }
}
