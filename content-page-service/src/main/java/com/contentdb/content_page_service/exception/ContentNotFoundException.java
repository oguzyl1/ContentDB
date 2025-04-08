package com.contentdb.content_page_service.exception;

import org.springframework.http.HttpStatus;

public class ContentNotFoundException extends BaseException {

    public ContentNotFoundException(String message) {
        super(message, "NOT FOUND", HttpStatus.NOT_FOUND);
    }
}
