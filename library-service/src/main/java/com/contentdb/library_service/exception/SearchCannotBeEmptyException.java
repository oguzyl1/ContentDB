package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;

public class SearchCannotBeEmptyException extends BaseException {

    public SearchCannotBeEmptyException(String message) {
        super(message, "BAD REQUEST", HttpStatus.BAD_REQUEST);
    }
}
