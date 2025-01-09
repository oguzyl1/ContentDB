package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNameNotFoundException.class)
    public ResponseEntity<ExceptionMessage> handleUserNameNotFoundException(UserNameNotFoundException e) {
        ExceptionMessage exceptionMessage = e.getExceptionMessage();
        return new ResponseEntity<>(exceptionMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<ExceptionMessage> handleUsernameAlreadyExistException(UsernameAlreadyExistException e) {
        ExceptionMessage exceptionMessage = e.getExceptionMessage();
        return new ResponseEntity<>(exceptionMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ExceptionMessage> handleEmailAlreadyExistException(EmailAlreadyExistException e) {
        ExceptionMessage exceptionMessage = e.getExceptionMessage();
        return new ResponseEntity<>(exceptionMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordIsWeakException.class)
    public ResponseEntity<ExceptionMessage> handlePasswordIsWeakException(PasswordIsWeakException e) {
        ExceptionMessage exceptionMessage = e.getExceptionMessage();
        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OldPasswordIsIncorrectException.class)
    public ResponseEntity<ExceptionMessage> handleOldPasswordIsIncorrectException(OldPasswordIsIncorrectException e) {
        ExceptionMessage exceptionMessage = e.getExceptionMessage();
        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
    }
}
