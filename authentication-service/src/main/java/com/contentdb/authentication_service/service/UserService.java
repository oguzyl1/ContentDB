package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.dto.UserListDto;
import com.contentdb.authentication_service.exception.*;
import com.contentdb.authentication_service.model.RefreshToken;
import com.contentdb.authentication_service.model.Role;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.RefreshTokenRepository;
import com.contentdb.authentication_service.repository.UserRepository;
import com.contentdb.authentication_service.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Validated
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION_MINUTES = 30;

    // Rate limiting için
    private final Map<String, Integer> loginAttemptCache = new ConcurrentHashMap<>();
    private final Map<String, Instant> lockedAccounts = new ConcurrentHashMap<>();


    public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }


    /**
     * Kullanıcı adı ile kullanıcıyı bularak getirir
     *
     * @param username the username identifying the user whose data is required.
     * @return kullanıcıyı döner
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new UserNotFoundException(username));
    }


    /**
     * Kullanıcıyı ID'ye göre bulur ve döndürür
     *
     * @param userId Kullanıcının ID'si
     * @return Bulunan kullanıcının DTO hali
     * @throws UserNotFoundException Kullanıcı bulunamazsa
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID: " + userId));

        if (user.isDeleted()) {
            throw new UserNotFoundException("ID: " + userId);
        }

        return UserDto.convertToUserDto(user);
    }


    /**
     * Kullanıcı girişi gerçekleştirir. Giriş başarılı ise access token oluşturur.
     *
     * @param loginRequest Kullanıcı adı ve şifresini içeren DTO.
     * @return Oluşturulan access ve refresh token.
     */
    @Transactional
    public TokenResponse login(LoginRequest loginRequest) {

        if (isAccountLocked(loginRequest.username())) {
            throw new ThisAccountLockedException(LOCK_TIME_DURATION_MINUTES);
        }

        User user = findUserByUsername(loginRequest.username());

        try {
            Authentication authentication = authenticationManager.
                    authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.username(),
                                    loginRequest.password())
                    );

            if (authentication.isAuthenticated()) {
                resetFailedAttempts(loginRequest.username());

                List<String> roles = user.getAuthorities().stream()
                        .map(Role::getValue)
                        .toList();
                String accessToken = jwtService.generateToken(loginRequest.username(), user.getId(), roles);
                String refreshToken = createRefreshToken(accessToken);

                user.setLastLoginTime(LocalDateTime.now());
                userRepository.save(user);

                logger.info("User {} successfully logged in", loginRequest.username());

                return new TokenResponse(accessToken, refreshToken);
            }
            throw new AuthenticationFailedException();

        } catch (BadCredentialsException e) {
            incrementFailedAttempts(loginRequest.username());
            throw new AuthenticationFailedException();
        }

    }


    /**
     * Kullanıcı çıkış işlemini gerçekleştirir ve refresh token silinir.
     *
     * @param username kullanıcı adı.
     */
    @Transactional
    public void logout(String username) {
        User user = findUserByUsername(username);
        refreshTokenRepository.deleteByUser(user);
        logger.info("User {} logged out", username);
    }


    /**
     * Kullanıcı bilgilerini günceller. Güncelleme başarılı ise güncellenmiş UserDto nesnesini döndürür.
     *
     * @param username          Güncellenecek kullanıcının mevcut kullanıcı adı.
     * @param updateUserRequest Güncelleme istek bilgilerini içeren DTO.
     * @return Güncellenmiş kullanıcı bilgilerini içeren UserDto.
     */
    @Transactional
    public UserDto updateUser(@Valid String username, @Valid UpdateUserRequest updateUserRequest) {

        User existingUser = findUserByUsername(username);


        if (!username.equals(updateUserRequest.getUsername())) {
            Optional<User> userWithNewUsername = userRepository.findByUsername(updateUserRequest.getUsername());
            if (userWithNewUsername.isPresent()) {
                throw new UsernameAlreadyExistException(updateUserRequest.getUsername());
            }
        }


        if (!existingUser.getEmail().equals(updateUserRequest.getEmail())) {
            Optional<User> userWithExistingEmail = userRepository.findByEmail(updateUserRequest.getEmail());
            if (userWithExistingEmail.isPresent()) {
                throw new EmailAlreadyExistException(updateUserRequest.getEmail());
            }
        }


        if (!existingUser.getAuthorities().equals(updateUserRequest.getAuthorities())) {
            boolean isAdmin = existingUser.getAuthorities().stream()
                    .anyMatch(authority -> authority.toString().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                throw new UnauthorizedAccessException();
            }

            long adminCount = userRepository.countAdmins();
            boolean isLastAdmin = isAdmin && adminCount == 1 &&
                    !updateUserRequest.getAuthorities().contains("ROLE_ADMIN");

            if (isLastAdmin) {
                throw new UnauthorizedAccessException();
            }
        }

        logger.info("User update requested for {}: Old values - name:{}, lastname:{}, email:{}, roles:{}",
                username, existingUser.getName(), existingUser.getLastName(),
                existingUser.getEmail(), existingUser.getAuthorities());


        existingUser.setName(updateUserRequest.getName());
        existingUser.setLastName(updateUserRequest.getLastName());
        existingUser.setUsername(updateUserRequest.getUsername());
        existingUser.setAuthorities(updateUserRequest.getAuthorities());
        existingUser.setEmail(updateUserRequest.getEmail());

        User savedUser = userRepository.save(existingUser);
        emailService.sendUserUpdatedMail(updateUserRequest.getEmail());

        logger.info("User {} updated successfully", username);

        return UserDto.convertToUserDto(savedUser);
    }


    /**
     * Kullanıcı kayıt olma işlemini gerçekleştiren metod
     *
     * @param createUserRequest Kaydedilecek kullanıcının gerekli bilgileri
     * @return kayıt olan kullanıcının bilgilerini döner.
     */
    @Transactional
    public UserDto createUser(@Valid CreateUserRequest createUserRequest) {

        if (userRepository.findByUsername(createUserRequest.username()).isPresent()) {
            throw new UsernameAlreadyExistException(createUserRequest.username());
        }

        if (userRepository.findByEmail(createUserRequest.email()).isPresent()) {
            throw new EmailAlreadyExistException(createUserRequest.email());
        }

        if (!isValidPassword(createUserRequest.password())) {
            throw new PasswordIsWeakException();
        }

        User newUser = new User.Builder()
                .name(createUserRequest.name())
                .lastName(createUserRequest.lastName())
                .email(createUserRequest.email())
                .username(createUserRequest.username())
                .password(bCryptPasswordEncoder.encode(createUserRequest.password()))
                .authorities(createUserRequest.authorities())
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .isEnabled(true)
                .accountNonLocked(true)
                .build();

        newUser.setDeleted(false);
        newUser.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(newUser);
        logger.info("New user created: {}", createUserRequest.username());

        return UserDto.convertToUserDto(savedUser);
    }


    /**
     * Tüm kullanıcıları listeler. (Soft delete yapılmış kullanıcılar hariç tutulur.)
     *
     * @return Soft delete yapılmamış kullanıcıların DTO halindeki listesini döndürür.
     */
    @Transactional(readOnly = true)
    public List<UserListDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.isDeleted())
                .map(UserListDto::converToUserListDto)
                .collect(Collectors.toList());
    }


    /**
     * Kullanıcıyı soft delete (silinmiş olarak işaretleme) yapar.
     *
     * @param username Silinecek kullanıcının kullanıcı adı.
     */
    @Transactional
    public void softDeleteUser(String username) {

        User user = findUserByUsername(username);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.toString().equals(ROLE_ADMIN));

        if (isAdmin && userRepository.countAdmins() == 1) {
            throw new UnauthorizedAccessException();
        }

        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User {} marked as deleted", username);
    }


    /**
     * Kullanıcının şifre değiştirme işlemi
     *
     * @param changePasswordRequest eski şifre ve yeni şifre bilgileri
     */
    @Transactional
    public void changePassword(@Valid String username ,@Valid ChangePasswordRequest changePasswordRequest) {


        User user = findUserByUsername(username);

        if (changePasswordRequest.newPassword().equals(changePasswordRequest.oldPassword())) {
            throw new PasswordsCannotBeTheSameException();
        }

        if (!bCryptPasswordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new OldPasswordIsIncorrectException();
        }

        if (!isValidPassword(changePasswordRequest.newPassword())) {
            throw new PasswordIsWeakException();
        }


        user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);

        emailService.sendPasswordChangeNotification(user.getEmail());
        logger.info("Password changed for user {}", username);
    }


    @Transactional
    public String createRefreshToken(String accessToken) {
        String username = jwtService.extractUsername(accessToken);
        User user = findUserByUsername(username);

        if (!jwtService.validateToken(accessToken, user)) {
            throw new UnauthorizedAccessException();
        }

        // Mevcut refresh token'ı bul
        List<RefreshToken> existingTokens = refreshTokenRepository.findByUser(user);

        if (!existingTokens.isEmpty()) {
            logger.debug("Eski refresh token mevcut, siliniyor... Kullanıcı ID: {}", user.getId());
            // Eski token'ı sil
            refreshTokenRepository.deleteByUser(user);
        }

        // Yeni refresh token oluştur
        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername(), user.getId());
        String hashedToken = hashToken(newRefreshToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(hashedToken);
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS));

        // Yeni token'ı kaydet
        logger.debug("Yeni refresh token kaydediliyor: {}", hashedToken);
        refreshTokenRepository.save(refreshToken);

        return newRefreshToken;
    }




    /**
     * Access token'ı yeniler
     *
     * @param refreshToken Refresh token
     * @return Yeni access token ve refresh token
     */
    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        String hashedToken = hashToken(refreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByToken(hashedToken)
                .orElseThrow(InvalidTokenException::new);

        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new InvalidTokenException();
        }

        User user = storedToken.getUser();

        List<String> roles = user.getAuthorities()
                .stream()
                .map(Role::getValue)
                .toList();

        String newAccessToken = jwtService.generateToken(user.getUsername(), user.getId(), roles);
        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername(), user.getId());
        String hashedNewToken = hashToken(newRefreshToken);


        storedToken.setToken(hashedNewToken);
        storedToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS));
        refreshTokenRepository.save(storedToken);

        logger.info("Access token refreshed for user {}", user.getUsername());

        return new TokenResponse(newAccessToken, newRefreshToken);
    }


    /**
     * Şifre sıfırlama işlemi başlatılır. Kullanıcının e-posta adresine reset token gönderilir.
     *
     * @param initiatePasswordResetRequest şifre sıfırlamak isteyen kullanıcının sisteme kayıtlı e posta adresi
     */
    @Transactional
    public void initiatePasswordReset(InitiatePasswordResetRequest initiatePasswordResetRequest) {

        User user = userRepository.findByEmail(initiatePasswordResetRequest.email())
                .orElseThrow(() -> new EmailNotFoundException(initiatePasswordResetRequest.email()));

        String passwordResetToken = jwtService.generateResetToken(user.getId(), user.getEmail());
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

        if (!isValidPassword(request.newPassword())) {
            throw new PasswordIsWeakException();
        }

        user.setPassword(bCryptPasswordEncoder.encode(request.newPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setResetTokenCreatedAt(null);
        userRepository.save(user);
        logger.info("Password reset completed for user {}", user.getUsername());
    }


    /**
     * Kullanıcı rollerini günceller
     *
     * @param username               Kullanıcı adı
     * @param updateUserRolesRequest Yeni roller
     * @return Güncellenen kullanıcı
     */
    @Transactional
    public UserDto updateUserRoles(String username, UpdateUserRolesRequest updateUserRolesRequest) {
        User user = findUserByUsername(username);

        logger.info("Updating roles for user {}. Old roles: {}", username,
                user.getAuthorities().stream().map(Role::toString).collect(Collectors.joining(", ")));

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.toString().equals(ROLE_ADMIN));

        boolean willRemainAdmin = updateUserRolesRequest.newRoles().contains(ROLE_ADMIN);

        if (isAdmin && !willRemainAdmin && userRepository.countAdmins() == 1) {
            throw new UnauthorizedAccessException();
        }

        user.setAuthorities(updateUserRolesRequest.newRoles()
                .stream()
                .map(Role::valueOf)
                .collect(Collectors.toSet()));

        User savedUser = userRepository.save(user);

        logger.info("Roles updated for user {}. New roles: {}", username,
                savedUser.getAuthorities().stream().map(Role::toString).collect(Collectors.joining(", ")));

        return UserDto.convertToUserDto(savedUser);
    }


    /**
     * Kullanıcı hesabını kilitleyip kilidini açma işlemi
     *
     * @param username Kilitlenecek/kilidi açılacak kullanıcının adı
     * @param locked   Kilit durumu (true: kilitli, false: açık)
     * @return Güncellenen kullanıcı bilgileri
     */
    @Transactional
    public UserDto toggleAccountLock(String username, boolean locked) {
        User user = findUserByUsername(username);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.toString().equals(ROLE_ADMIN));

        if (isAdmin && locked && userRepository.countAdmins() == 1) {
            throw new UnauthorizedAccessException();
        }

        user.setAccountNonLocked(!locked);
        User savedUser = userRepository.save(user);

        logger.info("Account {} {} by admin", username, locked ? "locked" : "unlocked");

        if (!locked) {
            lockedAccounts.remove(username);
            resetFailedAttempts(username);
        }

        return UserDto.convertToUserDto(savedUser);


    }

    /**
     * Son giriş zamanına göre aktif olmayan kullanıcıları listeler
     *
     * @param days Kaç gündür aktif olmayan kullanıcılar listelenecek
     * @return Aktif olmayan kullanıcılar listesi
     */
    @Transactional(readOnly = true)
    public List<UserListDto> getInactiveUsers(int days) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);

        return userRepository.findByLastLoginTimeBefore(cutoffDate)
                .stream()
                .filter(user -> !user.isDeleted())
                .map(UserListDto::converToUserListDto)
                .collect(Collectors.toList());
    }


    /**
     * Girilen şifrenin geçerli bir formatta olup olmadığını kontrol eder.
     *
     * @param password Şifre
     * @return Geçerli ise true, aksi halde false.
     */
    private Boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=!~?<>|])(?=.{8,25}$).*$";
        return password.matches(passwordPattern);
    }


    /**
     * Token'ı güvenli bir şekilde hashler
     *
     * @param token Orijinal token
     * @return Hashlenmiş token
     */
    private String hashToken(String token) {
        return bCryptPasswordEncoder.encode(token);
    }


    /**
     * Kullanıcı adına göre kullanıcıyı bulur (Yardımcı metod)
     *
     * @param username Kullanıcı adı
     * @return Bulunan kullanıcı
     * @throws UserNotFoundException Kullanıcı bulunamazsa
     */
    private User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (user.isDeleted()) {
            throw new UserNotFoundException(username);
        }

        return user;
    }


    /**
     * Başarısız giriş denemelerini artırır ve gerekirse hesabı kilitler
     *
     * @param username Kullanıcı adı
     */
    private void incrementFailedAttempts(String username) {
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
     * Başarısız giriş denemelerini sıfırlar
     *
     * @param username Kullanıcı adı
     */
    private void resetFailedAttempts(String username) {
        loginAttemptCache.remove(username);
    }


    /**
     * Kullanıcı hesabını kilitler
     *
     * @param username Kullanıcı adı
     */
    private void lockAccount(String username) {
        lockedAccounts.put(username, Instant.now().plus(LOCK_TIME_DURATION_MINUTES, ChronoUnit.MINUTES));
        logger.warn("Account {} locked due to multiple failed attempts", username);
    }


    /**
     * Hesabın kilitli olup olmadığını kontrol eder
     *
     * @param username Kullanıcı adı
     * @return Hesap kilitli ise true
     */
    private boolean isAccountLocked(String username) {
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


}

