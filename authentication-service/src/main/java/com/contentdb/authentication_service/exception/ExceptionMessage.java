package com.contentdb.authentication_service.exception;

public record ExceptionMessage(String timeStamp,
                               int statusCode,
                               String error,
                               String errorMessage,
                               String path,
                               String details) {
}
