package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;

public class PageNotCompleteException extends BaseException{
    public PageNotCompleteException(String message) {
        super(message, "NOT COMPLETE", HttpStatus.BAD_REQUEST);
    }
}
