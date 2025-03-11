package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.dto.UserListDto;
import com.contentdb.authentication_service.exception.UserNotFoundException;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.UserRepository;
import com.contentdb.authentication_service.request.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    private final AuthenticationService authenticationService;
    private final UserManagementService userManagementService;
    private final PasswordService passwordService;
    private final AccountLockService accountLockService;
    private final UserQueryService userQueryService;
    private final UserRepository userRepository;

    public UserService(AuthenticationService authenticationService, UserManagementService userManagementService, PasswordService passwordService, AccountLockService accountLockService, UserQueryService userQueryService,UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.userManagementService = userManagementService;
        this.passwordService = passwordService;
        this.accountLockService = accountLockService;
        this.userQueryService = userQueryService;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new UserNotFoundException(username));
    }


    public UserDto getUserById(String id) {
        return userQueryService.getUserById(id);
    }

    public TokenResponse login(LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }

    public Object updateUserInformation(String id, UpdateUserRequest request, boolean isAdmin) {
        return userManagementService.updateUser(id, request, isAdmin);
    }

    public UserDto createUser(CreateUserRequest request) {
        return userManagementService.createUser(request);
    }

    public List<UserListDto> getAllUsers() {
        return userManagementService.getAllUsers();
    }

    public void softDeleteUser(String id) {
        userManagementService.softDeleteUser(id);
    }

    public void changePassword(ChangePasswordRequest request) {
        passwordService.changePassword(request);
    }

    public TokenResponse refreshAccessToken(UserDetails user) {
        return authenticationService.refreshAccessToken(user);
    }

    public void initiatePasswordReset(InitiatePasswordResetRequest request) {
        authenticationService.initiatePasswordReset(request);
    }

    public void completePasswordReset(String token, ResetPasswordRequest request) {
        authenticationService.completePasswordReset(token, request);
    }

    public UserDto updateUserRoles(String id, UpdateUserRolesRequest request) {
        return userManagementService.updateUserRoles(id, request);
    }

    public UserDto toggleAccountLock(String id, boolean locked) {
        return accountLockService.toggleAccountLock(id, locked);
    }

    public List<UserListDto> getInactiveUsers(int day) {
        return userManagementService.getInactiveUsers(day);
    }

    public void incrementFailedAttempts(String username) {
        accountLockService.incrementFailedAttempts(username);
    }

    public void resetFailedAttempts(String username) {
        accountLockService.resetFailedAttempts(username);
    }

    public void lockAccount(String username) {
        accountLockService.lockAccount(username);
    }

    public boolean isAccountLocked(String username) {
        return accountLockService.isAccountLocked(username);
    }


}

//    private final UserRepository userRepository;
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
//    private final AuthenticationManager authenticationManager;
//    private final JwtService jwtService;
//    private final EmailService emailService;
//
//    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
//
//    private static final String ROLE_ADMIN = "ROLE_ADMIN";
//    private static final int REFRESH_TOKEN_EXPIRY_DAYS = 7;
//    private static final int MAX_FAILED_ATTEMPTS = 5;
//    private static final long LOCK_TIME_DURATION_MINUTES = 30;
//
//
//    private final Map<String, Integer> loginAttemptCache = new ConcurrentHashMap<>();
//    private final Map<String, Instant> lockedAccounts = new ConcurrentHashMap<>();
//
//
//    public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, EmailService emailService) {
//        this.userRepository = userRepository;
//        this.refreshTokenRepository = refreshTokenRepository;
//        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
//        this.authenticationManager = authenticationManager;
//        this.jwtService = jwtService;
//        this.emailService = emailService;
//    }
//
//
//    /**
//     * Kullanıcı adı ile kullanıcıyı bularak getirir
//     *
//     * @param username the username identifying the user whose data is required.
//     * @return kullanıcıyı döner
//     */
//    @Transactional(readOnly = true)
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> user = userRepository.findByUsername(username);
//        return user.orElseThrow(() -> new UserNotFoundException(username));
//    }
//
//
//    /**
//     * Kullanıcıyı ID'ye göre bulur ve döndürür
//     *
//     * @param userId Kullanıcının ID'si
//     * @return Bulunan kullanıcının DTO hali
//     * @throws UserNotFoundException Kullanıcı bulunamazsa
//     */
//    @Transactional(readOnly = true)
//    public UserDto getUserById(String userId) {
//        validateAccessToken();
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("ID: " + userId));
//
//        if (user.isDeleted()) {
//            throw new UserNotFoundException("ID: " + userId);
//        }
//
//        return UserDto.convertToUserDto(user);
//    }
//
//
//    /**
//     * Kullanıcı girişi gerçekleştirir. Giriş başarılı ise access token oluşturur.
//     *
//     * @param loginRequest Kullanıcı adı ve şifresini içeren DTO.
//     * @return Oluşturulan access ve refresh token.
//     */
//    @Transactional
//    public TokenResponse login(LoginRequest loginRequest) {
//
//        if (isAccountLocked(loginRequest.username())) {
//            throw new ThisAccountLockedException(LOCK_TIME_DURATION_MINUTES);
//        }
//
//        logger.info("Kullanıcı girişi yapılmaya çalışılıyor: {}", loginRequest.username());
//
//        User user = findUserByUsername(loginRequest.username());
//
//        logger.info("Kullanıcı bilgileri getirildi: {}", user.getUsername());
//
//        try {
//            Authentication authentication = authenticationManager.
//                    authenticate(
//                            new UsernamePasswordAuthenticationToken(
//                                    loginRequest.username(),
//                                    loginRequest.password())
//                    );
//
//
//            if (authentication.isAuthenticated()) {
//
//                logger.info("Kullanıcı başarıyla giriş işlemi başlıyor: {}", user.getUsername());
//
//                resetFailedAttempts(loginRequest.username());
//
//                List<String> roles = user.getAuthorities().stream()
//                        .map(Role::getValue)
//                        .toList();
//
//                String accessToken = jwtService.generateToken(loginRequest.username(), user.getId(), roles);
//
//                logger.info("Access token başarıyla oluştu: {}", accessToken);
//
//                String refreshToken = jwtService.generateRefreshToken(loginRequest.username(), user.getId(), roles);
//
//                RefreshToken newRefreshToken = new RefreshToken();
//                newRefreshToken.setToken(refreshToken);
//                newRefreshToken.setUser(user);
//                newRefreshToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
//                refreshTokenRepository.save(newRefreshToken);
//
//                logger.info("Refresh token başarıyla oluştu: {}", refreshToken);
//
//                user.setLastLoginTime(LocalDateTime.now());
//                userRepository.save(user);
//
//                logger.info("Kullanıcı başarıyla giriş yaptı: {}", user.getUsername());
//
//                return new TokenResponse(accessToken, refreshToken);
//            }
//            throw new AuthenticationFailedException();
//
//        } catch (BadCredentialsException e) {
//            logger.info("Kullanıcı girişi başarısız oldu: {}", user.getUsername());
//            incrementFailedAttempts(loginRequest.username());
//            throw new AuthenticationFailedException();
//        }
//
//    }
//
//
//    /**
//     * Kullanıcı çıkış işlemini gerçekleştirir ve refresh token silinir.
//     *
//     * @param userId kullanıcı id.
//     @Transactional public void logout(String userId) {
//     User user = findUserById(userId);
//     refreshTokenRepository.deleteByUser(user);
//     logger.info("Kullanıcı çıkış yaptı: {}", user.getUsername());
//     }
//     */
//
//
//    /**
//     * Kullanıcı bilgilerini günceller. Güncelleme başarılı ise güncellenmiş UserDto nesnesini döndürür.
//     *
//     * @param userId            Güncellenecek kullanıcının mevcut kullanıcı id'si.
//     * @param updateUserRequest Güncelleme istek bilgilerini içeren DTO.
//     * @return Güncellenmiş kullanıcı bilgilerini içeren UserDto.
//     */
//    @Transactional
//    public Object updateUser(@Valid String userId, @Valid UpdateUserRequest updateUserRequest, boolean isAdminRequest) {
//
//        logger.info("Access token kontrolü yapılıyor");
//        validateAccessToken();
//        logger.info("Access token kontrolü başarılı");
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
//        String currentUserId = currentUser.getId();
//
//        if (!isAdminRequest && !currentUserId.equals(userId)) {
//            throw new UnauthorizedAccessException();
//        }
//
//        logger.info("Kullanıcı bilgisi güncelleme işlemi başlatılıyor");
//
//        User existingUser = findUserById(userId);
//        logger.info("Kullanıcı bilgileri getirildi: {}", existingUser.getUsername());
//
//
//        String newUsername = updateUserRequest.getUsername();
//        if (newUsername != null && !existingUser.getUsername().equals(newUsername)) {
//            userRepository.findByUsername(newUsername)
//                    .ifPresent(user -> {
//                        throw new UsernameAlreadyExistException(newUsername);
//                    });
//        }
//        logger.info("Kullanıcı adı kontrolü yapıldı: {}", newUsername);
//
//
//        String newEmail = updateUserRequest.getEmail();
//        if (newEmail != null && !existingUser.getEmail().equals(newEmail)) {
//            userRepository.findByEmail(newEmail)
//                    .ifPresent(user -> {
//                        throw new EmailAlreadyExistException(newEmail);
//                    });
//        }
//        logger.info("Mail kontrolü yapıldı: {}", newEmail);
//
//
//        Set<Role> newAuthorities = updateUserRequest.getAuthorities();
//        boolean isCurrentUserAdmin = existingUser.getAuthorities().stream()
//                .anyMatch(role -> role.getValue().equals("ROLE_ADMIN"));
//
//        if (newAuthorities != null && !Objects.equals(existingUser.getAuthorities(), newAuthorities)) {
//            if (!isCurrentUserAdmin) {
//                throw new UnauthorizedAccessException();
//            }
//
//            long adminCount = userRepository.countAdmins();
//            boolean isLastAdmin = isCurrentUserAdmin && adminCount == 1 &&
//                    newAuthorities.stream().noneMatch(role -> role.getValue().equals("ROLE_ADMIN"));
//
//            if (isLastAdmin) {
//                throw new UnauthorizedAccessException();
//            }
//        }
//        logger.info("Rol köntrolü yapıldı: {}", newAuthorities);
//
//        logger.info("User update requested for {}: Old values - name:{}, lastname:{}, email:{}, roles:{}",
//                existingUser.getUsername(), existingUser.getName(), existingUser.getLastName(),
//                existingUser.getEmail(), existingUser.getAuthorities());
//
//
//        if (updateUserRequest.getName() != null) {
//            existingUser.setName(updateUserRequest.getName());
//        }
//        if (updateUserRequest.getLastName() != null) {
//            existingUser.setLastName(updateUserRequest.getLastName());
//        }
//        if (newUsername != null) {
//            existingUser.setUsername(newUsername);
//        }
//        if (newAuthorities != null) {
//            existingUser.setAuthorities(newAuthorities);
//        }
//        if (newEmail != null) {
//            existingUser.setEmail(newEmail);
//        }
//
//
//        User savedUser = userRepository.save(existingUser);
//
//        emailService.sendUserUpdatedMail(savedUser.getEmail());
//        logger.info("Kullanıcı bilgileri başarıyla güncellendi: {}", savedUser.getUsername());
//
//        List<String> roles = savedUser.getAuthorities()
//                .stream()
//                .map(Role::getValue)
//                .toList();
//
//        boolean shouldUpdateTokens = (!isAdminRequest || currentUserId.equals(userId));
//
//        if (shouldUpdateTokens) {
//            Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserId(userId);
//            String newAccessToken = jwtService.generateToken(savedUser.getUsername(), savedUser.getId(), roles);
//            String newRefreshToken = jwtService.generateRefreshToken(savedUser.getUsername(), savedUser.getId(), roles);
//
//            if (existingRefreshToken.isPresent()) {
//                RefreshToken token = existingRefreshToken.get();
//                token.setToken(newRefreshToken);
//                token.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
//                refreshTokenRepository.save(token);
//            } else {
//                RefreshToken saveRefresh = new RefreshToken();
//                saveRefresh.setToken(newRefreshToken);
//                saveRefresh.setUser(savedUser);
//                saveRefresh.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
//                refreshTokenRepository.save(saveRefresh);
//            }
//
//            UserResponseDto response = new UserResponseDto();
//            response.setUser(UserDto.convertToUserDto(savedUser));
//            response.setAccessToken(newAccessToken);
//            response.setRefreshToken(newRefreshToken);
//
//            return response;
//        } else {
//            return UserDto.convertToUserDto(savedUser);
//        }
//    }
//
//
//    /**
//     * Kullanıcı kayıt olma işlemini gerçekleştiren metod
//     *
//     * @param createUserRequest Kaydedilecek kullanıcının gerekli bilgileri
//     * @return kayıt olan kullanıcının bilgilerini döner.
//     */
//    @Transactional
//    public UserDto createUser(@Valid CreateUserRequest createUserRequest) {
//
//        logger.info("Kullanıcı kayıt işlemi başlatılıyor.");
//
//        if (userRepository.findByUsername(createUserRequest.username()).isPresent()) {
//            throw new UsernameAlreadyExistException(createUserRequest.username());
//        }
//
//
//        if (userRepository.findByEmail(createUserRequest.email()).isPresent()) {
//            throw new EmailAlreadyExistException(createUserRequest.email());
//        }
//
//        if (!isValidPassword(createUserRequest.password())) {
//            throw new PasswordIsWeakException();
//        }
//
//        User newUser = new User.Builder()
//                .name(createUserRequest.name())
//                .lastName(createUserRequest.lastName())
//                .email(createUserRequest.email())
//                .username(createUserRequest.username())
//                .password(bCryptPasswordEncoder.encode(createUserRequest.password()))
//                .authorities(createUserRequest.authorities())
//                .accountNonExpired(true)
//                .credentialsNonExpired(true)
//                .isEnabled(true)
//                .accountNonLocked(true)
//                .build();
//
//        newUser.setDeleted(false);
//        newUser.setCreatedAt(LocalDateTime.now());
//
//        User savedUser = userRepository.save(newUser);
//        logger.info("Yeni kullanıcı oluşturuldu: {}", createUserRequest.username());
//
//        return UserDto.convertToUserDto(savedUser);
//    }
//
//
//    /**
//     * Tüm kullanıcıları listeler. (Soft delete yapılmış kullanıcılar hariç tutulur.)
//     *
//     * @return Soft delete yapılmamış kullanıcıların DTO halindeki listesini döndürür.
//     */
//    @Transactional(readOnly = true)
//    public List<UserListDto> getAllUsers() {
//        logger.info("Soft delete yapılmamış kullanıcıların listesi getiriliyor");
//        return userRepository.findAll()
//                .stream()
//                .filter(user -> !user.isDeleted())
//                .map(UserListDto::converToUserListDto)
//                .collect(Collectors.toList());
//    }
//
//
//    /**
//     * Kullanıcıyı soft delete (silinmiş olarak işaretleme) yapar.
//     *
//     * @param username Silinecek kullanıcının kullanıcı adı.
//     */
//    @Transactional
//    public void softDeleteUser(String username) {
//
//        logger.info("Soft delete işlemi yapılıyor: {}", username);
//        User user = findUserByUsername(username);
//
//        boolean isAdmin = user.getAuthorities().stream()
//                .anyMatch(authority -> authority.toString().equals(ROLE_ADMIN));
//
//        if (isAdmin && userRepository.countAdmins() == 1) {
//            throw new UnauthorizedAccessException();
//        }
//
//        user.setDeleted(true);
//        user.setDeletedAt(LocalDateTime.now());
//        userRepository.save(user);
//
//        logger.info("User {} marked as deleted", username);
//    }
//
//
//    /**
//     * Kullanıcının şifre değiştirme işlemi
//     *
//     * @param changePasswordRequest eski şifre ve yeni şifre bilgileri
//     */
//    @Transactional
//    public void changePassword(@Valid String username, @Valid ChangePasswordRequest changePasswordRequest) {
//
//
//        User user = findUserByUsername(username);
//
//        if (changePasswordRequest.newPassword().equals(changePasswordRequest.oldPassword())) {
//            throw new PasswordsCannotBeTheSameException();
//        }
//
//        if (!bCryptPasswordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
//            throw new OldPasswordIsIncorrectException();
//        }
//
//        if (!isValidPassword(changePasswordRequest.newPassword())) {
//            throw new PasswordIsWeakException();
//        }
//
//
//        user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.newPassword()));
//        user.setPasswordChangedAt(LocalDateTime.now());
//        userRepository.save(user);
//
//        emailService.sendPasswordChangeNotification(user.getEmail());
//        logger.info("Password changed for user {}", username);
//    }
//
//
//    @Transactional
//    public String createRefreshToken(String accessToken) {
//
//        logger.info("Access token ile Refresh Token üretme işlemi başlıyor: {}", accessToken);
//
//        String username = jwtService.extractUser(accessToken);
//
//        logger.info("Tokendan kullanıcı adı çıkarıldı: {}", username);
//
//        User user = findUserByUsername(username);
//
//        logger.info("Kullanıcı bilgileri getirildi: {}", username);
//
//        if (!jwtService.validateToken(accessToken, user)) {
//            logger.warn("Access Token validate işlemi başarısız.");
//            throw new UnauthorizedAccessException();
//        }
//
//        refreshTokenRepository.deleteByUser(user);
//        logger.info("Eski Refresh token silindi.");
//
//        List<String> roles = user.getAuthorities()
//                .stream()
//                .map(Role::getValue)
//                .toList();
//
//        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername(), user.getId(), roles);
//
//        logger.info("Yeni Refresh token oluşturuldu : {}", newRefreshToken);
//
//        RefreshToken refreshToken = new RefreshToken();
//        refreshToken.setToken(newRefreshToken);
//        refreshToken.setUser(user);
//        refreshToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS));
//
//        refreshTokenRepository.save(refreshToken);
//
//        logger.debug("Yeni refresh token kaydedildi. {}", newRefreshToken);
//
//        return newRefreshToken;
//    }
//
//
//    /**
//     * Access token'ı yeniler
//     *
//     * @param refreshToken Refresh token
//     * @return Yeni access token ve refresh token
//     */
//    @Transactional
//    public TokenResponse refreshAccessToken(String refreshToken) {
//        String hashedToken = hashToken(refreshToken);
//
//        RefreshToken storedToken = refreshTokenRepository.findByToken(hashedToken)
//                .orElseThrow(InvalidTokenException::new);
//
//        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
//            refreshTokenRepository.delete(storedToken);
//            throw new InvalidTokenException();
//        }
//
//        User user = storedToken.getUser();
//
//        List<String> roles = user.getAuthorities()
//                .stream()
//                .map(Role::getValue)
//                .toList();
//
//        String newAccessToken = jwtService.generateToken(user.getUsername(), user.getId(), roles);
//        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername(), user.getId(), roles);
//        String hashedNewToken = hashToken(newRefreshToken);
//
//
//        storedToken.setToken(hashedNewToken);
//        storedToken.setExpiryDate(Instant.now().plus(REFRESH_TOKEN_EXPIRY_DAYS, ChronoUnit.DAYS));
//        refreshTokenRepository.save(storedToken);
//
//        logger.info("Access token refreshed for user {}", user.getUsername());
//
//        return new TokenResponse(newAccessToken, newRefreshToken);
//    }
//
//
//    /**
//     * Şifre sıfırlama işlemi başlatılır. Kullanıcının e-posta adresine reset token gönderilir.
//     *
//     * @param initiatePasswordResetRequest şifre sıfırlamak isteyen kullanıcının sisteme kayıtlı e posta adresi
//     */
//    @Transactional
//    public void initiatePasswordReset(InitiatePasswordResetRequest initiatePasswordResetRequest) {
//
//        User user = userRepository.findByEmail(initiatePasswordResetRequest.email())
//                .orElseThrow(() -> new EmailNotFoundException(initiatePasswordResetRequest.email()));
//
//        String passwordResetToken = jwtService.generateResetToken(user.getId(), user.getEmail());
//        emailService.sendPasswordResetEmail(user.getEmail(), passwordResetToken);
//
//        user.setResetTokenCreatedAt(LocalDateTime.now());
//        userRepository.save(user);
//
//        logger.info("Password reset initiated for user with email {}", initiatePasswordResetRequest.email());
//    }
//
//
//    /**
//     * Şifre sıfırlama işlemini tamamlar. Reset token ve yeni şifre ile şifre güncellenir.
//     *
//     * @param resetToken kullanıcının mail adresine gelen reset token
//     * @param request    kullanıcıdan alınancak yeni şifre
//     */
//    @Transactional
//    public void completePasswordReset(String resetToken, ResetPasswordRequest request) {
//
//        if (!jwtService.validateResetToken(resetToken)) {
//            throw new InvalidTokenException();
//        }
//
//        String userId = jwtService.extractUserId(resetToken);
//        User user = userRepository.findById(userId).orElseThrow(InvalidTokenException::new);
//
//        if (user.getResetTokenCreatedAt() != null &&
//                user.getResetTokenCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
//            throw new InvalidTokenException();
//        }
//
//        if (!request.newPassword().equals(request.newPasswordAgain())) {
//            throw new PasswordIsNotSameException();
//        }
//
//        if (!isValidPassword(request.newPassword())) {
//            throw new PasswordIsWeakException();
//        }
//
//        user.setPassword(bCryptPasswordEncoder.encode(request.newPassword()));
//        user.setPasswordChangedAt(LocalDateTime.now());
//        user.setResetTokenCreatedAt(null);
//        userRepository.save(user);
//        logger.info("Password reset completed for user {}", user.getUsername());
//    }
//
//
//    /**
//     * Kullanıcı rollerini günceller
//     *
//     * @param username               Kullanıcı adı
//     * @param updateUserRolesRequest Yeni roller
//     * @return Güncellenen kullanıcı
//     */
//    @Transactional
//    public UserDto updateUserRoles(String username, UpdateUserRolesRequest updateUserRolesRequest) {
//        User user = findUserByUsername(username);
//
//        logger.info("Updating roles for user {}. Old roles: {}", username,
//                user.getAuthorities().stream().map(Role::toString).collect(Collectors.joining(", ")));
//
//        boolean isAdmin = user.getAuthorities().stream()
//                .anyMatch(authority -> authority.toString().equals(ROLE_ADMIN));
//
//        boolean willRemainAdmin = updateUserRolesRequest.newRoles().contains(ROLE_ADMIN);
//
//        if (isAdmin && !willRemainAdmin && userRepository.countAdmins() == 1) {
//            throw new UnauthorizedAccessException();
//        }
//
//        user.setAuthorities(updateUserRolesRequest.newRoles()
//                .stream()
//                .map(Role::valueOf)
//                .collect(Collectors.toSet()));
//
//        User savedUser = userRepository.save(user);
//
//        logger.info("Roles updated for user {}. New roles: {}", username,
//                savedUser.getAuthorities().stream().map(Role::toString).collect(Collectors.joining(", ")));
//
//        return UserDto.convertToUserDto(savedUser);
//    }
//
//
//    /**
//     * Kullanıcı hesabını kilitleyip kilidini açma işlemi
//     *
//     * @param username Kilitlenecek/kilidi açılacak kullanıcının adı
//     * @param locked   Kilit durumu (true: kilitli, false: açık)
//     * @return Güncellenen kullanıcı bilgileri
//     */
//    @Transactional
//    public UserDto toggleAccountLock(String username, boolean locked) {
//        User user = findUserByUsername(username);
//
//        boolean isAdmin = user.getAuthorities().stream()
//                .anyMatch(authority -> authority.toString().equals(ROLE_ADMIN));
//
//        if (isAdmin && locked && userRepository.countAdmins() == 1) {
//            throw new UnauthorizedAccessException();
//        }
//
//        user.setAccountNonLocked(!locked);
//        User savedUser = userRepository.save(user);
//
//        logger.info("Account {} {} by admin", username, locked ? "locked" : "unlocked");
//
//        if (!locked) {
//            lockedAccounts.remove(username);
//            resetFailedAttempts(username);
//        }
//
//        return UserDto.convertToUserDto(savedUser);
//
//
//    }
//
//    /**
//     * Son giriş zamanına göre aktif olmayan kullanıcıları listeler
//     *
//     * @param days Kaç gündür aktif olmayan kullanıcılar listelenecek
//     * @return Aktif olmayan kullanıcılar listesi
//     */
//    @Transactional(readOnly = true)
//    public List<UserListDto> getInactiveUsers(int days) {
//        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
//
//        return userRepository.findByLastLoginTimeBefore(cutoffDate)
//                .stream()
//                .filter(user -> !user.isDeleted())
//                .map(UserListDto::converToUserListDto)
//                .collect(Collectors.toList());
//    }
//
//
//    /**
//     * Girilen şifrenin geçerli bir formatta olup olmadığını kontrol eder.
//     *
//     * @param password Şifre
//     * @return Geçerli ise true, aksi halde false.
//     */
//    private Boolean isValidPassword(String password) {
//        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=!~?<>|])(?=.{8,25}$).*$";
//        return password.matches(passwordPattern);
//    }
//
//
//    /**
//     * Token'ı güvenli bir şekilde hashler
//     *
//     * @param token Orijinal token
//     * @return Hashlenmiş token
//     */
//    private String hashToken(String token) {
//        return bCryptPasswordEncoder.encode(token);
//    }
//
//
//    /**
//     * Kullanıcı adına göre kullanıcıyı bulur (Yardımcı metod)
//     *
//     * @param username Kullanıcı adı
//     * @return Bulunan kullanıcı
//     * @throws UserNotFoundException Kullanıcı bulunamazsa
//     */
//    private User findUserByUsername(String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UserNotFoundException(username));
//
//        if (user.isDeleted()) {
//            throw new UserNotFoundException(username);
//        }
//
//        return user;
//    }
//
//    private User findUserById(String userId) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
//        if (user.isDeleted()) {
//            throw new UserNotFoundException(userId);
//        }
//        return user;
//    }
//
//    /**
//     * Başarısız giriş denemelerini artırır ve gerekirse hesabı kilitler
//     *
//     * @param username Kullanıcı adı
//     */
//    private void incrementFailedAttempts(String username) {
//        int attempts = loginAttemptCache.getOrDefault(username, 0);
//        attempts++;
//
//        if (attempts >= MAX_FAILED_ATTEMPTS) {
//            lockAccount(username);
//            loginAttemptCache.remove(username);
//        } else {
//            loginAttemptCache.put(username, attempts);
//        }
//
//        logger.warn("Failed login attempt {} for user {}", attempts, username);
//
//    }
//
//    /**
//     * Başarısız giriş denemelerini sıfırlar
//     *
//     * @param username Kullanıcı adı
//     */
//    private void resetFailedAttempts(String username) {
//        loginAttemptCache.remove(username);
//    }
//
//
//    /**
//     * Kullanıcı hesabını kilitler
//     *
//     * @param username Kullanıcı adı
//     */
//    private void lockAccount(String username) {
//        lockedAccounts.put(username, Instant.now().plus(LOCK_TIME_DURATION_MINUTES, ChronoUnit.MINUTES));
//        logger.warn("Account {} locked due to multiple failed attempts", username);
//    }
//
//
//    /**
//     * Hesabın kilitli olup olmadığını kontrol eder
//     *
//     * @param username Kullanıcı adı
//     * @return Hesap kilitli ise true
//     */
//    private boolean isAccountLocked(String username) {
//        if (lockedAccounts.containsKey(username)) {
//            Instant lockTime = lockedAccounts.get(username);
//
//            if (Instant.now().isAfter(lockTime)) {
//                lockedAccounts.remove(username);
//                return false;
//            }
//            return true;
//        }
//        return false;
//    }
//
//
//    private void validateAccessToken() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//            String authHeader = request.getHeader("Authorization");
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                Claims claims = jwtService.extractAllClaims(token);
//                String tokenType = claims.get("type", String.class);
//                if (!"access".equals(tokenType)) {
//                    throw new UnauthorizedAccessException();
//                }
//            }
//        }
//    }
// }


