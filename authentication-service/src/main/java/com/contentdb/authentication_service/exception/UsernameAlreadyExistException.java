package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


public class UsernameAlreadyExistException extends BaseException {

    public UsernameAlreadyExistException(String username) {
        super("Bu kullanıcı adı kullanılmakta", "CONFLICT", HttpStatus.CONFLICT);
    }
}
