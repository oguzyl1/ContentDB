package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class OldPasswordIsIncorrectException extends RuntimeException {

    private final ExceptionMessage exceptionMessage;

    public OldPasswordIsIncorrectException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.errorMessage());
        this.exceptionMessage = exceptionMessage;
    }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }

}
