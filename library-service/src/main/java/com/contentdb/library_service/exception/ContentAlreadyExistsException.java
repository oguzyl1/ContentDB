package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;

public class ContentAlreadyExistsException extends BaseException {
    public ContentAlreadyExistsException(String message) {
        super(message, "CONFLICT", HttpStatus.CONFLICT);
    }
}
