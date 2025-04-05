package com.contentdb.comment_service.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends BaseException {

    public CommentNotFoundException(String message) {
        super(message, "NOT FOUND", HttpStatus.NOT_FOUND);
    }
}
