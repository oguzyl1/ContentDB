package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.dto.UserListDto;
import com.contentdb.authentication_service.dto.UserResponseDto;
import com.contentdb.authentication_service.exception.EmailAlreadyExistException;
import com.contentdb.authentication_service.exception.PasswordIsWeakException;
import com.contentdb.authentication_service.exception.UnauthorizedAccessException;
import com.contentdb.authentication_service.exception.UsernameAlreadyExistException;
import com.contentdb.authentication_service.model.RefreshToken;
import com.contentdb.authentication_service.model.Role;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.RefreshTokenRepository;
import com.contentdb.authentication_service.repository.UserRepository;
import com.contentdb.authentication_service.request.CreateUserRequest;
import com.contentdb.authentication_service.request.UpdateUserRequest;
import com.contentdb.authentication_service.request.UpdateUserRolesRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Validated
public class UserManagementService {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementService.class);
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final UserQueryService userQueryService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AccountLockService accountLockService;

    public UserManagementService(UserRepository userRepository, UserQueryService userQueryService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtService jwtService, EmailService emailService, RefreshTokenRepository refreshTokenRepository, AccountLockService accountLockService) {
        this.userRepository = userRepository;
        this.userQueryService = userQueryService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.accountLockService = accountLockService;
    }


    /**
     * Kullanıcı kayıt olma işlemini gerçekleştiren metod
     *
     * @param createUserRequest Kaydedilecek kullanıcının gerekli bilgileri
     * @return kayıt olan kullanıcının bilgilerini döner.
     */
    @Transactional
    public UserDto createUser(@NotEmpty CreateUserRequest createUserRequest) {

        logger.info("Kullanıcı kayıt işlemi başlatılıyor.");

        userRepository.findByUsernameOrEmail(createUserRequest.username(), createUserRequest.email())
                .ifPresent(user -> {
                    if (user.getUsername().equals(createUserRequest.username())) {
                        throw new UsernameAlreadyExistException(createUserRequest.username());
                    }
                    if (user.getEmail().equals(createUserRequest.email())) {
                        throw new EmailAlreadyExistException(createUserRequest.email());
                    }
                });


        if (!userQueryService.isValidPassword(createUserRequest.password())) {
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
        emailService.userCreated(savedUser.getEmail());
        logger.info("Yeni kullanıcı oluşturuldu: {}", createUserRequest.username());

        return UserDto.convertToUserDto(savedUser);
    }


    /**
     * Kullanıcı bilgilerini günceller. Güncelleme başarılı ise güncellenmiş UserDto nesnesini döndürür.
     *
     * @param userId            Güncellenecek kullanıcının mevcut kullanıcı id'si.
     * @param updateUserRequest Güncelleme istek bilgilerini içeren DTO.
     * @return Güncellenmiş kullanıcı bilgilerini içeren UserDto.
     */
    @Transactional
    public Object updateUser(@NotEmpty String userId, @NotEmpty UpdateUserRequest updateUserRequest, @NotEmpty boolean isAdminRequest) {

        logger.info("Access token kontrolü yapılıyor");
        jwtService.validateAccessToken();
        logger.info("Access token kontrolü başarılı");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        String currentUserId = currentUser.getId();

        if (!isAdminRequest && !currentUserId.equals(userId)) {
            throw new UnauthorizedAccessException();
        }

        logger.info("Kullanıcı bilgisi güncelleme işlemi başlatılıyor");


        User existingUser = userQueryService.findUserById(userId);
        logger.info("Kullanıcı bilgileri getirildi: {}", existingUser.getUsername());


        String newUsername = updateUserRequest.getUsername();
        if (newUsername != null && !existingUser.getUsername().equals(newUsername)) {
            userRepository.findByUsername(newUsername)
                    .ifPresent(user -> {
                        throw new UsernameAlreadyExistException(newUsername);
                    });
        }
        logger.info("Kullanıcı adı kontrolü yapıldı: {}", newUsername);


        String newEmail = updateUserRequest.getEmail();
        if (newEmail != null && !existingUser.getEmail().equals(newEmail)) {
            userRepository.findByEmail(newEmail)
                    .ifPresent(user -> {
                        throw new EmailAlreadyExistException(newEmail);
                    });
        }
        logger.info("Mail kontrolü yapıldı: {}", newEmail);

        Set<Role> newAuthorities = updateUserRequest.getAuthorities();

        userQueryService.validateAdminRoleChange(existingUser, newAuthorities, userRepository);
        logger.info("Rol köntrolü yapıldı: {}", newAuthorities);


        logger.info("User update requested for {}: Old values - name:{}, lastname:{}, email:{}, roles:{}",
                existingUser.getUsername(), existingUser.getName(), existingUser.getLastName(),
                existingUser.getEmail(), existingUser.getAuthorities());


        if (updateUserRequest.getName() != null) {
            existingUser.setName(updateUserRequest.getName());
        }
        if (updateUserRequest.getLastName() != null) {
            existingUser.setLastName(updateUserRequest.getLastName());
        }
        if (newUsername != null) {
            existingUser.setUsername(newUsername);
        }
        if (newAuthorities != null) {
            existingUser.setAuthorities(newAuthorities);
        }
        if (newEmail != null) {
            existingUser.setEmail(newEmail);
        }


        User savedUser = userRepository.save(existingUser);

        emailService.sendUserUpdatedMail(savedUser.getEmail());
        logger.info("Kullanıcı bilgileri başarıyla güncellendi: {}", savedUser.getUsername());

        List<String> roles = userQueryService.getRolesByUser(savedUser);

        boolean shouldUpdateTokens = (!isAdminRequest || currentUserId.equals(userId));
        if (shouldUpdateTokens) {
            Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUserId(userId);
            String newAccessToken = jwtService.generateToken(savedUser.getUsername(), savedUser.getId(), roles);
            String newRefreshToken = jwtService.generateRefreshToken(savedUser.getUsername(), savedUser.getId(), roles);

            if (existingRefreshToken.isPresent()) {
                RefreshToken token = existingRefreshToken.get();
                token.setToken(newRefreshToken);
                token.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
                refreshTokenRepository.save(token);
            } else {
                RefreshToken saveRefresh = new RefreshToken();
                saveRefresh.setToken(newRefreshToken);
                saveRefresh.setUser(savedUser);
                saveRefresh.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
                refreshTokenRepository.save(saveRefresh);
            }

            UserResponseDto response = new UserResponseDto();
            response.setUser(UserDto.convertToUserDto(savedUser));
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);

            return response;
        } else {
            return UserDto.convertToUserDto(savedUser);
        }
    }


    /**
     * Kullanıcıyı soft delete (silinmiş olarak işaretleme) yapar.
     *
     * @param userId Silinecek kullanıcının kullanıcı id.
     */
    @Transactional
    public void softDeleteUser(@NotEmpty String userId) {

        logger.info("Soft delete işlemi yapılıyor: {}", userId);
        User user = userQueryService.findUserById(userId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        boolean isCurrentUserAdmin = currentUser.getAuthorities().stream()
                .anyMatch(authority -> authority.toString().equals(ROLE_ADMIN));

        if (!isCurrentUserAdmin && !currentUser.getId().equals(userId)) {
            throw new UnauthorizedAccessException();
        }

        boolean isUserToDeleteAdmin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.toString().equals(ROLE_ADMIN));
        if (isUserToDeleteAdmin && userRepository.countAdmins() == 1) {
            throw new UnauthorizedAccessException();
        }

        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);

        logger.info("User {} marked as deleted", user.getUsername());
    }


    /**
     * Kullanıcı rollerini günceller
     *
     * @param userId                 kullanıcı id
     * @param updateUserRolesRequest Yeni roller
     * @return Güncellenen kullanıcı
     */
    @Transactional
    public UserDto updateUserRoles(@NotEmpty String userId, @NotEmpty UpdateUserRolesRequest updateUserRolesRequest) {

        User user = userQueryService.findUserById(userId);

        logger.info("{} adlı kullanıcının rolü güncelleniyor. Eski rol: {}", user.getUsername(),
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

        logger.info("{} adlı kullanıcın rolü güncelleniyor. New roles: {}", user.getUsername(),
                savedUser.getAuthorities().stream().map(Role::toString).collect(Collectors.joining(", ")));

        return UserDto.convertToUserDto(savedUser);
    }


    /**
     * Tüm kullanıcıları listeler. (Soft delete yapılmış kullanıcılar hariç tutulur.)
     *
     * @return Soft delete yapılmamış kullanıcıların DTO halindeki listesini döndürür.
     */
    @Transactional(readOnly = true)
    public List<UserListDto> getAllUsers() {
        logger.info("Soft delete yapılmamış kullanıcıların listesi getiriliyor");
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.isDeleted())
                .map(UserListDto::converToUserListDto)
                .collect(Collectors.toList());
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


}
