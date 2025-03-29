package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;

public class ListNotFoundException extends BaseException {

    public ListNotFoundException(String message) {
        super(message, "NOT FOUND", HttpStatus.NOT_FOUND);
    }

}
