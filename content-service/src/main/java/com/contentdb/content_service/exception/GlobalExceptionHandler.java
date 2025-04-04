package com.contentdb.content_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionMessage> handleCustomException(BaseException ex, WebRequest request) {
        return createErrorResponse(ex.getStatus(), ex.getMessage(), request.getDescription(false));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessage> handleGlobalException(Exception ex, WebRequest request) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Beklenmeyen bir hata oluştu.", request.getDescription(false));
    }

    private ResponseEntity<ExceptionMessage> createErrorResponse(HttpStatus status, String message, String path) {
        ExceptionMessage errorResponse = new ExceptionMessage(
                LocalDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                "Lütfen yaptığınız işlemi tekrar kontrol edin. Sorun devam ediyorsa destek ekibiyle iletişime geçin."
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
