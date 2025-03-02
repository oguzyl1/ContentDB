package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class EmailNotFoundException extends BaseException {

    public EmailNotFoundException(String email) {
        super("Bu email adresine sahip kullanıcı bulunamadı" + email, "USER_EMAIL_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
}
