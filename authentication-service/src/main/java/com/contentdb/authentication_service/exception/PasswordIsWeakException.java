package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PasswordIsWeakException extends RuntimeException {

    private final ExceptionMessage exceptionMessage;

    public PasswordIsWeakException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.errorMessage());
        this.exceptionMessage = exceptionMessage;
    }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }

}
