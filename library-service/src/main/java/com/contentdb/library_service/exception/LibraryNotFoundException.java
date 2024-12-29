package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LibraryNotFoundException extends RuntimeException {
    ExceptionMessage exceptionMessage;

    public LibraryNotFoundException(ExceptionMessage exceptionMessage) {
        super(exceptionMessage.toString());
        this.exceptionMessage = exceptionMessage;
    }

    public ExceptionMessage getExceptionMessage() {
        return exceptionMessage;
    }
}
