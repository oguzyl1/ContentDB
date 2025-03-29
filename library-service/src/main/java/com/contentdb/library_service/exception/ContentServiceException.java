package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;

public class ContentServiceException extends BaseException {
    public ContentServiceException(String message) {
        super(message, "EXPECTATION FAILED", HttpStatus.EXPECTATION_FAILED);
    }
}
