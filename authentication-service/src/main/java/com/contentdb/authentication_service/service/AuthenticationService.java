package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.exception.*;
import com.contentdb.authentication_service.model.RefreshToken;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.RefreshTokenRepository;
import com.contentdb.authentication_service.repository.UserRepository;
import com.contentdb.authentication_service.request.InitiatePasswordResetRequest;
import com.contentdb.authentication_service.request.LoginRequest;
import com.contentdb.authentication_service.request.ResetPasswordRequest;
import com.contentdb.authentication_service.request.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AuthenticationService {


    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;
    private static final long LOCK_TIME_DURATION_MINUTES = 30;


    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AccountLockService accountLockService;
    private final UserQueryService userQueryService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public AuthenticationService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, AuthenticationManager authenticationManager, JwtService jwtService, AccountLockService accountLockService, UserQueryService userQueryService, EmailService emailService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.accountLockService = accountLockService;
        this.userQueryService = userQueryService;
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    /**
     * Kullanıcı girişi gerçekleştirir. Giriş başarılı ise access token oluşturur.
     *
     * @param loginRequest Kullanıcı adı ve şifresini içeren DTO.
     * @return Oluşturulan access ve refresh token.
     */
    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {

        if (accountLockService.isAccountLocked(loginRequest.username())) {
            throw new ThisAccountLockedException(LOCK_TIME_DURATION_MINUTES);
        }

        logger.info("Kullanıcı girişi yapılmaya çalışılıyor: {}", loginRequest.username());
        User user = userQueryService.findUserByUsername(loginRequest.username());
        logger.info("Kullanıcı bilgileri getirildi: {}", user.getUsername());


        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.username(),
                    loginRequest.password())
            );

            if (authentication.isAuthenticated()) {

                logger.info("Kullanıcı başarıyla giriş işlemi başlıyor: {}", user.getUsername());
                accountLockService.resetFailedAttempts(loginRequest.username());

                List<String> roles = userQueryService.getRolesByUser(user);

                String accessToken = jwtService.generateToken(loginRequest.username(), user.getId(), roles);
                logger.info("Access token başarıyla oluştu: {}", accessToken);

                String refreshToken = jwtService.generateRefreshToken(loginRequest.username(), user.getId(), roles);
                logger.info("Refresh Token başarıyla oluştu.: {}", refreshToken);


                RefreshToken newRefreshToken = new RefreshToken();
                newRefreshToken.setToken(refreshToken);
                newRefreshToken.setUser(user);
                newRefreshToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
                refreshTokenRepository.save(newRefreshToken);
                logger.info("Refresh token başarıyla oluştu: {}", refreshToken);

                user.setLastLoginTime(LocalDateTime.now());
                userRepository.save(user);
                logger.info("Kullanıcı başarıyla giriş yaptı: {}", user.getUsername());


                return new TokenResponse(accessToken);
            }
            throw new AuthenticationFailedException();

        } catch (BadCredentialsException e) {
            logger.info("Kullanıcı girişi başarısız oldu: {}", user.getUsername());
            accountLockService.incrementFailedAttempts(loginRequest.username());
            throw new AuthenticationFailedException();
        }

    }


    /**
     * Access token'ı yeniler
     *
     * @param userDetails kullanıcı
     * @return Yeni access token ve refresh token
     */
    @Transactional
    public TokenResponse refreshAccessToken(UserDetails userDetails) {

        User user = (User) userDetails;
        String userId = user.getId();

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId).orElseThrow(UnauthorizedAccessException::new);

        jwtService.validateRefreshToken(refreshToken.getToken());

        if (!jwtService.validateRefreshToken(refreshToken.getToken())) {
            throw new InvalidTokenException();
        }

        List<String> roles = userQueryService.getRolesByUser(user);

        String newAccessToken = jwtService.generateToken(user.getUsername(), user.getId(), roles);

        Instant currentInstant = Instant.now();
        long remainingTime = ChronoUnit.MILLIS.between(currentInstant, refreshToken.getExpiryDate());

        if (remainingTime < 30 * 60 * 1000) {
            String newRefreshToken = jwtService.generateRefreshToken(user.getUsername(), user.getId(), roles);
            refreshToken.setToken(newRefreshToken);
            refreshToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS));
            refreshTokenRepository.save(refreshToken);
        }

        logger.info("Access token refreshed for user {}", user.getUsername());

        return new TokenResponse(newAccessToken);
    }


    /**
     * Şifre sıfırlama işlemi başlatılır. Kullanıcının e-posta adresine reset token gönderilir.
     *
     * @param initiatePasswordResetRequest şifre sıfırlamak isteyen kullanıcının sisteme kayıtlı e posta adresi
     */
    @Transactional
    public void initiatePasswordReset(InitiatePasswordResetRequest initiatePasswordResetRequest) {

        User user = userQueryService.findUserByEmail(initiatePasswordResetRequest.email());

        String passwordResetToken = jwtService.generateResetToken(user.getUsername(),user.getId(),user.getEmail());

        emailService.sendPasswordResetEmail(user.getEmail(), passwordResetToken);
        user.setResetTokenCreatedAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("Password reset initiated for user with email {}", initiatePasswordResetRequest.email());
    }


    /**
     * Şifre sıfırlama işlemini tamamlar. Reset token ve yeni şifre ile şifre güncellenir.
     *
     * @param resetToken kullanıcının mail adresine gelen reset token
     * @param request    kullanıcıdan alınancak yeni şifre
     */
    @Transactional
    public void completePasswordReset(String resetToken, ResetPasswordRequest request) {

        if (!jwtService.validateResetToken(resetToken)) {
            throw new InvalidTokenException();
        }

        String userId = jwtService.extractUserId(resetToken);
        User user = userRepository.findById(userId).orElseThrow(InvalidTokenException::new);

        if (user.getResetTokenCreatedAt() != null &&
                user.getResetTokenCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException();
        }

        if (!request.newPassword().equals(request.newPasswordAgain())) {
            throw new PasswordIsNotSameException();
        }

        if (!userQueryService.isValidPassword(request.newPassword())) {
            throw new PasswordIsWeakException();
        }

        user.setPassword(bCryptPasswordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setResetTokenCreatedAt(null);
        userRepository.save(user);
        logger.info("Password reset completed for user {}", user.getUsername());
    }


}
