package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ContentNotFoundException extends BaseException {

    public ContentNotFoundException(String message) {
        super(message, "NOT FOUND", HttpStatus.NOT_FOUND);
    }
}
