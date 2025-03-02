package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {

    public UserNotFoundException(String username) {
        super("Bu kullanıcı adı ile kayıtlı bir kullanıcı bulunamadı: " + username, "USERNAME_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
