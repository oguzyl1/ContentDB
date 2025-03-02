package com.contentdb.authentication_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionMessage> handleBaseException(BaseException ex, HttpServletRequest request) {
        ExceptionMessage response = new ExceptionMessage(
                LocalDateTime.now(),
                ex.getStatus().value(),
                ex.getStatus().getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI(),
                ex.getErrorCode()
        );
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessage> handleGenericException(Exception ex, HttpServletRequest request) {
        ExceptionMessage response = new ExceptionMessage(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Beklenmeyen bir hata oluştu",
                request.getRequestURI(),
                "INTERNAL_ERROR"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }



//    @ExceptionHandler(UserNameNotFoundException.class)
//    public ResponseEntity<ExceptionMessage> handleUserNameNotFoundException(UserNameNotFoundException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(UsernameAlreadyExistException.class)
//    public ResponseEntity<ExceptionMessage> handleUsernameAlreadyExistException(UsernameAlreadyExistException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(EmailAlreadyExistException.class)
//    public ResponseEntity<ExceptionMessage> handleEmailAlreadyExistException(EmailAlreadyExistException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.CONFLICT);
//    }
//
//    @ExceptionHandler(PasswordIsWeakException.class)
//    public ResponseEntity<ExceptionMessage> handlePasswordIsWeakException(PasswordIsWeakException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(OldPasswordIsIncorrectException.class)
//    public ResponseEntity<ExceptionMessage> handleOldPasswordIsIncorrectException(OldPasswordIsIncorrectException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(InvalidTokenException.class)
//    public ResponseEntity<ExceptionMessage> handleInvalidTokenException(InvalidTokenException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.UNAUTHORIZED);
//    }
//
//    @ExceptionHandler(EmailNotFoundException.class)
//    public ResponseEntity<ExceptionMessage> handleEmailNotFoundException(EmailNotFoundException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(TokenExpiredException.class)
//    public ResponseEntity<ExceptionMessage> handleTokenExpiredException(TokenExpiredException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(PasswordIsNotSameException.class)
//    public ResponseEntity<ExceptionMessage> handlePasswordIsNotSameException(PasswordIsNotSameException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(UserDeletedException.class)
//    public ResponseEntity<ExceptionMessage> handleUserDeletedException(UserDeletedException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(AuthenticationFailedException.class)
//    public ResponseEntity<ExceptionMessage> handleAuthenticationFailedException(AuthenticationFailedException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.UNAUTHORIZED);
//    }
//
//    @ExceptionHandler(UnauthorizedAccessException.class)
//    public ResponseEntity<ExceptionMessage> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
//        ExceptionMessage exceptionMessage = e.getExceptionMessage();
//        return new ResponseEntity<>(exceptionMessage, HttpStatus.UNAUTHORIZED);
//    }

}

