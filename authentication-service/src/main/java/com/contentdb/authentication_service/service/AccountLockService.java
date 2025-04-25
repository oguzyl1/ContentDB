package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.exception.UnauthorizedAccessException;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountLockService {

    private static final long LOCK_TIME_DURATION_MINUTES = 1;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final Logger logger = LoggerFactory.getLogger(AccountLockService.class);

    private final Map<String, Integer> loginAttemptCache = new ConcurrentHashMap<>();
    private final Map<String, Instant> lockedAccounts = new ConcurrentHashMap<>();


    private final UserRepository userRepository;
    private final UserQueryService userQueryService;


    public AccountLockService(UserRepository userRepository, UserQueryService userQueryService) {
        this.userRepository = userRepository;
        this.userQueryService = userQueryService;
    }


    /**
     * Kullanıcı hesabını kilitler
     *
     * @param username Kullanıcı adı
     */
    public void lockAccount(String username) {
        lockedAccounts.put(username, Instant.now().plus(LOCK_TIME_DURATION_MINUTES, ChronoUnit.MINUTES));
        logger.warn("Account {} locked due to multiple failed attempts", username);
    }


    /**
     * Hesabın kilitli olup olmadığını kontrol eder
     *
     * @param username Kullanıcı adı
     * @return Hesap kilitli ise true
     */
    public boolean isAccountLocked(String username) {
        if (lockedAccounts.containsKey(username)) {
            Instant lockTime = lockedAccounts.get(username);

            if (Instant.now().isAfter(lockTime)) {
                lockedAccounts.remove(username);
                return false;
            }
            return true;
        }
        return false;
    }


    /**
     * Başarısız giriş denemelerini sıfırlar
     *
     * @param username Kullanıcı adı
     */
    public void resetFailedAttempts(String username) {
        loginAttemptCache.remove(username);
    }


    /**
     * Başarısız giriş denemelerini artırır ve gerekirse hesabı kilitler
     *
     * @param username Kullanıcı adı
     */
    public void incrementFailedAttempts(String username) {
        int attempts = loginAttemptCache.getOrDefault(username, 0);
        attempts++;

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            lockAccount(username);
            loginAttemptCache.remove(username);
        } else {
            loginAttemptCache.put(username, attempts);
        }

        logger.warn("Failed login attempt {} for user {}", attempts, username);

    }


    /**
     * Kullanıcı hesabını kilitleyip kilidini açma işlemi
     *
     * @param userId Kilitlenecek/kilidi açılacak kullanıcı id
     * @param locked Kilit durumu (true: kilitli, false: açık)
     * @return Güncellenen kullanıcı bilgileri
     */
    @Transactional
    public UserDto toggleAccountLock(String userId, boolean locked) {

        User user = userQueryService.findUserById(userId);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.toString().equals(ROLE_ADMIN));

        if (isAdmin && locked && userRepository.countAdmins() == 1) {
            throw new UnauthorizedAccessException("Bu işlem için yetkiniz yok.");
        }

        user.setAccountNonLocked(!locked);
        User savedUser = userRepository.save(user);

        logger.info("Kullanıcı {} admin tarafından {}", user.getUsername(), locked ? "kitlendi" : "kilidi açıldı");

        if (!locked) {
            lockedAccounts.remove(user.getUsername());
            resetFailedAttempts(user.getUsername());
        }

        return UserDto.convertToUserDto(savedUser);


    }


}