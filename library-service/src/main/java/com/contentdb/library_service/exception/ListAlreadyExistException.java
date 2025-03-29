package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;


public class ListAlreadyExistException extends BaseException{

    public ListAlreadyExistException(String message) {
        super(message, "CONFLICT", HttpStatus.CONFLICT);
    }

}
