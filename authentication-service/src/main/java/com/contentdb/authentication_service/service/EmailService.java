/**
 * daha sonra eklenecek
 */

package com.contentdb.authentication_service.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;  // Gönderen e-posta adresi

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void userCreated(String toEmail){
        String subject = "ContentDB Üyeliği Başarıyla Oluşturuldu";
        String body ="Aramıza hoş geldiniz. Dizi, film ve daha fazlası için sitemizi ziyaret edebilirsiniz.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);

    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetUrl = "http://localhost:8080/api/auth/password-reset/complete?token=" + token;
        String subject = "Şifre Sıfırlama Talebi";
        String body = "Şifrenizi sıfırlamak için aşağıdaki bağlantıya tıklayın:\n" + resetUrl;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }


    /**
     * Kullanıcının şifre değişikliği sonrası bildirim maili gönderir.
     *
     * @param toEmail Bildirimin gönderileceği e-posta adresi.
     */
    public void sendPasswordChangeNotification(String toEmail) {
        String subject = "Şifre Değişikliği Bildirimi";
        String body = "Hesabınızın şifresi başarıyla değiştirilmiştir. Eğer bu işlemi siz yapmadıysanız, lütfen derhal destek ekibimizle iletişime geçin.";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }


    public void sendUserUpdatedMail(String toEmail) {
        String subject = "Kullanıcı Bilgileri Değişikliği Bildirimi";
        String body = "Hesap bilgileriniz değiştirilmiştir. Eğer bu işlemi siz yapmadıysanız, lütfen derhal destek ekibimizle iletişime geçin.";
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}

