package com.contentdb.content_service.exception;

import org.springframework.http.HttpStatus;

public class ResponseEmptyException extends BaseException {
    public ResponseEmptyException() {
        super("TMDB API'den sonuç bulunamadı.", HttpStatus.NO_CONTENT);
    }
}
