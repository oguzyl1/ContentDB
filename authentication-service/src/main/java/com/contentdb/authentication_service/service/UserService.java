package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.dto.*;
import com.contentdb.authentication_service.exception.*;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import java.util.stream.Collectors;

@Validated
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

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

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

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

        return UserDto.convertToUserDto(userRepository.save(newUser));
    }

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

        Optional<User> existingUser2 = userRepository.findByUsername(updateUserRequest.getUsername());
        if (existingUser2.isPresent() && !existingUser2.get().getUsername().equals(updateUserRequest.getUsername())) {
            throw new UsernameAlreadyExistException(
                    new ExceptionMessage(
                            LocalDateTime.now().toString(),
                            HttpStatus.CONFLICT.value(),
                            "Conflict",
                            "Bu kullanıcı adı zaten kullanılmakta: " + updateUserRequest.getUsername(),
                            "/authentication/createUser",
                            "Bu kullanıcı adı zaten kullanılmakta lütfen farklı bir kullanıcı adı ile kayıt olun. Kullanıcı adı: " + updateUserRequest.getUsername()
                    ));
        }

        existingUser.setName(updateUserRequest.getName());
        existingUser.setLastName(updateUserRequest.getLastName());
        existingUser.setUsername(updateUserRequest.getUsername());
        existingUser.setAuthorities(updateUserRequest.getAuthorities());

        return UserDto.convertToUserDto(userRepository.save(existingUser));
    }

    @Transactional
    public void changePassword(@Valid String username, @Valid PasswordResetRequest passwordResetRequest) {
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

        if (passwordResetRequest.newPassword().equals(passwordResetRequest.oldPassword())) {
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


        if (!bCryptPasswordEncoder.matches(passwordResetRequest.oldPassword(), user.getPassword())) {
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

        if (!isValidPassword(passwordResetRequest.newPassword())) {
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

        user.setPassword(bCryptPasswordEncoder.encode(passwordResetRequest.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public List<UserListDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserListDto::converToUserListDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public String generateToken(CreateUserRequest createUserRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(createUserRequest.username(), createUserRequest.password()));
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(createUserRequest.username());
        }
        throw new UserNameNotFoundException(
                new ExceptionMessage(
                        LocalDateTime.now().toString(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found",
                        "Bu isme Sahip kullanıcı bulunamadı: " + createUserRequest.username(),
                        "/authentication/loadUserByUsername",
                        "Aranan kullanıcı adı ile bir kayıt bulunamadı.Kullanıcı adı: " + createUserRequest.username()
                )
        );
    }

    private Boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=!~?<>|])(?=.{8,25}$).*$";
        return password.matches(passwordPattern);
    }


}
