package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class EmailNotFoundException extends BaseException {

    public EmailNotFoundException(String message) {
        super(message, "NOT FOUND", HttpStatus.NOT_FOUND);
    }
}
