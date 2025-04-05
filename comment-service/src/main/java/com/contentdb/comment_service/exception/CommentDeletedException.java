package com.contentdb.comment_service.exception;

import org.springframework.http.HttpStatus;

public class CommentDeletedException extends BaseException {

    public CommentDeletedException(String message) {
        super(message, "BAD REQUEST", HttpStatus.BAD_REQUEST);
    }
}
