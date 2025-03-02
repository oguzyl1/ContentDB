package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class ThisAccountLockedException extends BaseException {

    public ThisAccountLockedException(long time) {
        super("Hesabınız kilitlendi. " + time + " dakika sonra tekrar deneyin.", "BAD_REQUEST", HttpStatus.BAD_REQUEST);
    }
}
