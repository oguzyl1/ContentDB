package com.contentdb.library_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LibraryAlreadyExistException.class)
    public ResponseEntity<ExceptionMessage> handleLibraryAlreadyExistException(LibraryAlreadyExistException e) {
        ExceptionMessage exceptionMessage = e.getExceptionMessage();
        return new ResponseEntity<>(exceptionMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LibraryNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleLibraryNotFoundException(LibraryNotFoundException e) {
        ExceptionMessage exceptionMessage = e.getExceptionMessage();
        return new ResponseEntity<>(exceptionMessage, HttpStatus.NOT_FOUND);
    }
}
