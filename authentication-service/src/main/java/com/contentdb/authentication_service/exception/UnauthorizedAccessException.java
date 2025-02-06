package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedAccessException extends RuntimeException {

    private final ExceptionMessage exceptionMessage;

    public UnauthorizedAccessException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.errorMessage());
        this.exceptionMessage = exceptionMessage;
    }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }

}
