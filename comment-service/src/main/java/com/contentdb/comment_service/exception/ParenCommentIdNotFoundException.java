package com.contentdb.comment_service.exception;

import org.springframework.http.HttpStatus;

public class ParenCommentIdNotFoundException extends BaseException {

    public ParenCommentIdNotFoundException(String message) {
        super(message, "NOT FOUND", HttpStatus.NOT_FOUND);
    }
}
