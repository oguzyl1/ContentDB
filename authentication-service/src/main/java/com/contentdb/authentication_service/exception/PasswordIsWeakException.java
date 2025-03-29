package com.contentdb.authentication_service.exception;

import org.springframework.http.HttpStatus;

public class PasswordIsWeakException extends BaseException {

    public PasswordIsWeakException() {
        super("Şifre güvenlik kurallarına uymuyor. Şifre en az bir büyük harf," +
                        " en az bir küçük harf ,en az bir rakam," +
                        " en az bir özel karakter içermelidir ve en az 8 ila 25 karakter" +
                        " uzunluğunda olmalıdır. Lütfen bu kurallara uygun şifre ile tekrar deneyin."

                , "BAD REQUEST",
                HttpStatus.BAD_REQUEST);
    }
}
