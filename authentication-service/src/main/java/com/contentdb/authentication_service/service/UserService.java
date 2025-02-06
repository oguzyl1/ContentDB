package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.dto.UserListDto;
import com.contentdb.authentication_service.exception.*;
import com.contentdb.authentication_service.model.Role;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.UserRepository;
import com.contentdb.authentication_service.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, EmailService emailService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }


    /**
     * Kullanıcı girişi gerçekleştirir. Giriş başarılı ise access token oluşturur.
     *
     * @param loginRequest Kullanıcı adı ve şifresini içeren DTO.
     * @return Oluşturulan access token.
     */
    @Transactional
    public String login(LoginRequest loginRequest) {

        User user = userRepository.findByUsername(loginRequest.username()).orElseThrow(() -> new UserNameNotFoundException(
                new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme Sahip kullanıcı bulunamadı: " + loginRequest.username(),
                        "/authentication/login",
                        "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + loginRequest.username()
                )
        ));

        if (user.isDeleted()) {
            throw new UserDeletedException(new ExceptionMessage(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad request",
                    "Bu isme Sahip kullanıcı bulunamadı: " + loginRequest.username(),
                    "/authentication/login",
                    "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + loginRequest.username()
            ));
        }


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
        );

        if (authentication.isAuthenticated()) {

            List<String> roles = user.getAuthorities().stream()
                    .map(Role::getValue)
                    .toList();

            return jwtService.generateToken(loginRequest.username(),
                    getUserIdByUsername(loginRequest.username()), roles);
        }

        throw new AuthenticationFailedException(new ExceptionMessage(
                LocalDateTime.now().toString(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Kimlik doğrulama işlemi başarısız oldu.",
                "/authentication/login",
                "Kimlik doğrulama işlemi başarısız oldu. Kullanıcı adı veya şifreyi kontrol ediniz."
        ));
    }


    /**
     * Kullanıcı bilgilerini günceller.
     */
    @Transactional
    public UserDto updateUser(@Valid String username, @Valid UpdateUserRequest updateUserRequest) {

        User existingUser = userRepository.findByUsername(username).orElseThrow(() -> new UserNameNotFoundException(
                new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme Sahip kullanıcı bulunamadı: " + username,
                        "/authentication/updateUser",
                        "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + username
                )
        ));

        if (!username.equals(updateUserRequest.getUsername())) {
            Optional<User> userWithNewUsername = userRepository.findByUsername(updateUserRequest.getUsername());
            if (userWithNewUsername.isPresent()) {
                throw new UsernameAlreadyExistException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.CONFLICT.value(),
                        "Conflict",
                        "Bu kullanıcı adı zaten kullanılmakta: " + updateUserRequest.getUsername(),
                        "/authentication/updateUser",
                        "Bu kullanıcı adı zaten kullanılmakta lütfen farklı bir kullanıcı adı ile kayıt olun. Kullanıcı adı: " + updateUserRequest.getUsername()
                ));
            }
        }

        if (!existingUser.getEmail().equals(updateUserRequest.getEmail())) {
            Optional<User> userWithExistingEmail = userRepository.findByEmail(updateUserRequest.getEmail());
            if (userWithExistingEmail.isPresent()) {
                throw new EmailAlreadyExistException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.CONFLICT.value(),
                        "Conflict",
                        "Bu email adresi zaten kullanılmakta: " + updateUserRequest.getEmail(),
                        "/authentication/updateUser",
                        "Bu email adresi zaten kullanılmakta lütfen farklı bir email adresi ile kayıt olun. Email adresi: " + updateUserRequest.getEmail()
                ));
            }
        }

        if (!existingUser.getAuthorities().equals(updateUserRequest.getAuthorities())) {
            boolean isAdmin = existingUser.getAuthorities().stream()
                    .anyMatch(authority -> authority.toString().equals("ROLE_ADMIN"));

            if (!isAdmin) {
                throw new UnauthorizedAccessException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized",
                        "Rol değişikliği yapmak için ADMIN yetkisine sahip olmanız gerekiyor.",
                        "/authentication/updateUser",
                        "Rol değişikliği yapmak için ADMIN yetkisine sahip olmanız gerekiyor.Sizin yetkiniz : " + updateUserRequest.getAuthorities()
                ));
            }

            // Son ADMIN'in rolünü değiştirmesini engelle
            long adminCount = userRepository.countAdmins();
            boolean isLastAdmin = isAdmin && adminCount == 1 &&
                    !updateUserRequest.getAuthorities().contains("ROLE_ADMIN");

            logger.info("admincount : {}", adminCount);
            logger.info("isAdmin : {}", isAdmin);


            if (isLastAdmin) {
                throw new UnauthorizedAccessException(
                        new ExceptionMessage(
                                LocalDateTime.now().toString(),
                                HttpStatus.UNAUTHORIZED.value(),
                                "Unauthorized",
                                "Sistemdeki son admin rolü bu kullanıcıda olduğu için rol değişikliği yapılamaz",
                                "/authentication/updateUser",
                                "Sistemdeki son admin rolü bu kullanıcıda olduğu için rol değişikliği yapılamaz.Bu hesabın rolünü değiştirmek istiyorsanız sisteme başka bir admin daha atayın."
                        ));
            }
        }


        existingUser.setName(updateUserRequest.getName());
        existingUser.setLastName(updateUserRequest.getLastName());
        existingUser.setUsername(updateUserRequest.getUsername());
        existingUser.setAuthorities(updateUserRequest.getAuthorities());
        existingUser.setEmail(updateUserRequest.getEmail());

        return UserDto.convertToUserDto(userRepository.save(existingUser));
    }


    /**
     * Kullanıcı adı ile kullanıcıyı bularak getirir
     *
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElseThrow(() -> new UserNameNotFoundException(
                new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme Sahip kullanıcı bulunamadı: " + username,
                        "/authentication/loadUserByUsername",
                        "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + username
                )
        ));
    }


    /**
     * Tüm kullanıcıları listeler. (Soft delete yapılmış kullanıcılar hariç tutulmalıdır.)
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
     * Kullanıcı kaydetme işlemi
     *
     * @param createUserRequest Kaydedilecek kullanıcının gerekli bilgileri
     * @return kaydedilen kullanıcının bilgilerini döner.
     */
    @Transactional
    public UserDto createUser(@Valid CreateUserRequest createUserRequest) {

        if (userRepository.findByUsername(createUserRequest.username()).isPresent()) {
            throw new UsernameAlreadyExistException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.CONFLICT.value(),
                            "Conflict",
                            "Bu kullanıcı adı zaten kullanılmakta: " + createUserRequest.username(),
                            "/authentication/createUser",
                            "Bu kullanıcı adı zaten kullanılmakta lütfen farklı bir kullanıcı adı ile kayıt olun. Kullanıcı adı: " + createUserRequest.username()
                    ));
        }

        if (userRepository.findByEmail(createUserRequest.email()).isPresent()) {
            throw new EmailAlreadyExistException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.CONFLICT.value(),
                            "Conflict",
                            "Bu email adresi zaten kullanılmakta: " + createUserRequest.username(),
                            "/authentication/createUser",
                            "Bu email adresi zaten kullanılmakta lütfen farklı bir email adresi ile kayıt olun. Email adresi: " + createUserRequest.username()
                    ));
        }

        if (!isValidPassword(createUserRequest.password())) {
            throw new PasswordIsWeakException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Bad request",
                            "Şifre güvenlik kurallarına uymuyor: ",
                            "/authentication/createUser",
                            "Şifre güvenlik kurallarına uymuyor. Şifre en az bir büyük harf, en az bir küçük harf ,en az bir rakam, en az bir özel karakter içermelidir ve en az 8 ila 25 karakter uzunluğunda olmalıdır. Lütfen bu kurallara uygun şifre ile tekrar deneyin."
                    ));
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
        return UserDto.convertToUserDto(userRepository.save(newUser));
    }


    /**
     * Kullanıcıyı soft delete (silinmiş olarak işaretleme) yapar.
     *
     * @param username Silinecek kullanıcının kullanıcı adı.
     */
    @Transactional
    public void softDeleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNameNotFoundException(
                new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme sahip kullanıcı bulunamadı: " + username,
                        "/authentication/softDeleteUser",
                        "Aranan kullanıcı adı ile bir kayıt bulunamadı. Kullanıcı adı: " + username
                ))
        );

        user.setDeleted(true);
        userRepository.save(user);
    }


    /**
     * Kullanıcının şifre değiştirme işlemi
     *
     * @param username              kullanıcı adı
     * @param changePasswordRequest eski şifre ve yeni şifre bilgileri
     */
    @Transactional
    public void changePassword(@Valid String username, @Valid ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNameNotFoundException(
                new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme Sahip kullanıcı bulunamadı: " + username,
                        "/authentication/changePassword",
                        "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + username
                )
        ));

        if (changePasswordRequest.newPassword().equals(changePasswordRequest.oldPassword())) {
            throw new PasswordIsWeakException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Bad request",
                            "Yeni şifre eski şifre ile aynı olamaz.",
                            "/authentication/changePassword",
                            "Yeni şifre eski şifre ile aynı olamaz. Lütfen farklı bir şifre girin."

                    ));
        }


        if (!bCryptPasswordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new OldPasswordIsIncorrectException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Bad request",
                            "Eski şifre hatalı.",
                            "/authentication/changePassword",
                            "Eski şifre hatalı. Lütfen şifreyi kontrol edin."
                    )
            );
        }

        if (!isValidPassword(changePasswordRequest.newPassword())) {
            throw new PasswordIsWeakException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.BAD_REQUEST.value(),
                            "Bad request",
                            "Yeni şifre güvenlik kurallarına uymuyor: ",
                            "/authentication/changePassword",
                            "Yeni şifre güvenlik kurallarına uymuyor. Şifre en az bir büyük harf, en az bir küçük harf ,en az bir rakam, en az bir özel karakter içermelidir ve en az 8 ila 25 karakter uzunluğunda olmalıdır. Lütfen bu kurallara uygun şifre ile tekrar deneyin."
                    ));
        }

        user.setPassword(bCryptPasswordEncoder.encode(changePasswordRequest.newPassword()));
        userRepository.save(user);
    }

    /**
     * Refresh token kullanarak yeni access token oluşturur.
     *
     * @param resetTokenRequest
     * @return jwt token döner
     */
    @Transactional
    public String resetToken(ResetTokenRequest resetTokenRequest) {
        String incomingRefreshToken = resetTokenRequest.refreshToken();
        if (!jwtService.validateRefreshToken(incomingRefreshToken)) {
            throw new InvalidTokenException(new ExceptionMessage(
                    LocalDateTime.now().toString(),
                    HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized",
                    "Refresh token geçersiz veya süresi dolmuş.",
                    "/authentication/resetToken",
                    "Refresh token geçersiz."
            ));
        }

        String username = jwtService.extractUser(incomingRefreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNameNotFoundException(
                new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme Sahip kullanıcı bulunamadı: " + username,
                        "/authentication/resetToken",
                        "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + username
                )

        ));

        if (user.isDeleted()) {
            throw new UserNameNotFoundException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.NOT_FOUND.value(),
                            "Not Found",
                            "Bu isme Sahip kullanıcı bulunamadı: " + username,
                            "/authentication/resetToken",
                            "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + username
                    )

            );

        }

        List<String> roles = user.getAuthorities()
                .stream()
                .map(Role::getValue)
                .toList();

        return jwtService.generateToken(username, user.getId(),roles);

    }


    @Transactional
    public String createRefreshToken(String username) {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNameNotFoundException(
                new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme Sahip kullanıcı bulunamadı: " + username,
                        "/authentication/createRefreshToken",
                        "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + username
                )));

        if (user.isDeleted()) {
            throw new UserNameNotFoundException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.NOT_FOUND.value(),
                            "Not Found",
                            "Bu isme Sahip kullanıcı bulunamadı: " + username,
                            "/authentication/createRefreshToken",
                            "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + username
                    )

            );

        }

        String refreshToken = jwtService.generateRefreshToken(username, user.getId());

        user.setResetToken(refreshToken);
        user.setResetTokenExpiry(LocalDateTime.now());
        userRepository.save(user);


        return refreshToken;
    }


    /**
     * Şifre sıfırlama işlemi başlatılır. Kullanıcının e-posta adresine reset token gönderilir.
     *
     * @param initiatePasswordResetRequest şifre sıfırlamak isteyen kullanıcının sisteme kayıtlı e posta adresi
     */
    @Transactional
    public void initiatePasswordReset(InitiatePasswordResetRequest initiatePasswordResetRequest) {
        User user = userRepository.findByEmail(initiatePasswordResetRequest.email())
                .orElseThrow(() -> new EmailNotFoundException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu email adresine sahip kullanıcı bulunamadı: " + initiatePasswordResetRequest.email(),
                        "/authentication/initiatePasswordReset",
                        "Email adresi bulunamadı: " + initiatePasswordResetRequest.email()
                )));
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
    }

    /**
     * Şifre sıfırlama işlemini tamamlar. Reset token ve yeni şifre ile şifre güncellenir.
     *
     * @param completePasswordResetRequest şifre yenileme isteği için gerekli olan token ve yeni şifre
     */
    @Transactional
    public void completePasswordReset(CompletePasswordResetRequest completePasswordResetRequest) {
        User user = userRepository.findByResetToken(completePasswordResetRequest.resetToken())
                .orElseThrow(() -> new InvalidTokenException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Bad request",
                        "Geçersiz şifre sıfırlama token'ı.",
                        "/authentication/completePasswordReset",
                        "Şifre sıfırlama token'ı bulunamadı veya geçersiz."
                )));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException(new ExceptionMessage(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad request",
                    "Şifre sıfırlama token'ı süresi dolmuş.",
                    "/authentication/completePasswordReset",
                    "Token süresi geçmiş. Yeni sıfırlama isteği gönderiniz."
            ));
        }

        if (!completePasswordResetRequest.resetPasswordRequest().newPassword().equals(completePasswordResetRequest.resetPasswordRequest().newPasswordAgain())) {
            throw new PasswordIsNotSameException(new ExceptionMessage(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad request",
                    "Şifreler aynı değil.",
                    "/authentication/completePasswordReset",
                    "Tekrar yazılması istenen şifre ilk yazılan şifre ile aynı değil. Lütfen kontrol ediniz."
            ));
        }

        if (!isValidPassword(completePasswordResetRequest.resetPasswordRequest().newPassword())) {
            throw new PasswordIsWeakException(new ExceptionMessage(
                    LocalDateTime.now().toString(),
                    HttpStatus.BAD_REQUEST.value(),
                    "Bad request",
                    "Yeni şifre güvenlik kurallarına uymuyor.",
                    "/authentication/completePasswordReset",
                    "Şifre, en az bir büyük harf, bir küçük harf, bir rakam, bir özel karakter içermeli ve 8-25 karakter olmalıdır."
            ));
        }

        user.setPassword(bCryptPasswordEncoder.encode(completePasswordResetRequest.resetPasswordRequest().newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }


    @Transactional
    public UserDto updateUserRoles(String username, UpdateUserRolesRequest updateUserRolesRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNameNotFoundException(new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme sahip kullanıcı bulunamadı: " + username,
                        "/authentication/updateUserRoles",
                        "Kullanıcı bulunamadı: " + username
                )));

        user.setAuthorities(updateUserRolesRequest.newRoles().stream().map(Role::valueOf).collect(Collectors.toSet()));
        return UserDto.convertToUserDto(userRepository.save(user));
    }


    private String createRefreshToken(String username, String userId) {
        return jwtService.generateRefreshToken(username, userId);
    }


    /**
     * Kullanıcının user id değerini getirir.
     *
     * @param username Kullanıcı Adı
     * @return kullanıcı adı ile bir kullanıcı var ise kullanıcının user id' sini döner.
     */
    private String getUserIdByUsername(String username) {
        return userRepository.findUserIdByUsername(username).orElseThrow(() -> new UserNameNotFoundException(
                new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not found",
                        "Kullanıcı adıyla kullanıcı bulunamadı: " + username,
                        "/authentication/getUserIdByUsername",
                        "Bu kullanıcı adıyla kullanıcı bulunamadı. Lütfen kullanıcı adını kontrol edin. Kullanıcı adı :" + username
                )));
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

}

