package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.exception.OldPasswordIsIncorrectException;
import com.contentdb.authentication_service.exception.PasswordIsWeakException;
import com.contentdb.authentication_service.exception.PasswordsCannotBeTheSameException;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.UserRepository;
import com.contentdb.authentication_service.request.ChangePasswordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Service
@Validated
public class PasswordService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserQueryService userQueryService;
    private final EmailService emailService;
    private final JwtService jwtService;

    public PasswordService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UserQueryService userQueryService, EmailService emailService, JwtService jwtService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userQueryService = userQueryService;
        this.emailService = emailService;
        this.jwtService = jwtService;
    }


    /**
     * Kullanıcının şifre değiştirme işlemi
     *
     * @param changePasswordRequest eski şifre ve yeni şifre bilgileri
     */
    @Transactional
    public void changePassword(@NotEmpty ChangePasswordRequest changePasswordRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        jwtService.validateAccessToken();

        if (changePasswordRequest.newPassword().equals(changePasswordRequest.oldPassword())) {
            throw new PasswordsCannotBeTheSameException();
        }

        if (!bCryptPasswordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new OldPasswordIsIncorrectException();
        }

        if (!userQueryService.isValidPassword(changePasswordRequest.newPassword())) {
            throw new PasswordIsWeakException();
        }

        user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendPasswordChangeNotification(user.getEmail());
        logger.info("Kullanıcının şifresi değiştirildi {}", user.getUsername());
    }
}
