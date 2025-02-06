/** daha sonra eklenecek */

package com.contentdb.authentication_service.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendPasswordResetEmail(String email, String resetToken) {
        // Gerçek email gönderimi yerine, sadece loglama yapıyoruz.
        logger.info("Şifre sıfırlama emaili gönderiliyor. Email: {}, Reset Token: {}", email, resetToken);
    }
}