package com.contentdb.comment_service.exception;

import org.springframework.http.HttpStatus;

public class CommentAlreadyMarkedException extends BaseException {

    public CommentAlreadyMarkedException(String message) {
        super(message, "CONFLİCT", HttpStatus.CONFLICT);
    }
}
