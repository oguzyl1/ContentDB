package com.contentdb.content_page_service.exception;

import java.time.LocalDateTime;


public record ExceptionMessage(
        LocalDateTime timeStamp,
        int statusCode,
        String error,
        String message,
        String path,
        String errorCode) {
}
