package com.contentdb.authentication_service.service;

import com.contentdb.authentication_service.dto.UserDto;
import com.contentdb.authentication_service.exception.EmailNotFoundException;
import com.contentdb.authentication_service.exception.UnauthorizedAccessException;
import com.contentdb.authentication_service.exception.UserNotFoundException;
import com.contentdb.authentication_service.model.Role;
import com.contentdb.authentication_service.model.User;
import com.contentdb.authentication_service.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class UserQueryService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserQueryService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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

        jwtService.validateAccessToken();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID: " + userId));

        if (user.isDeleted()) {
            throw new UserNotFoundException("ID: " + userId);
        }

        return UserDto.convertToUserDto(user);
    }


    /**
     * @param userId kullanıcı id
     * @return aranan kullanıcının bilgisi
     */
    @Transactional(readOnly = true)
    public User findUserById(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (user.isDeleted()) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }


    /**
     * Kullanıcı adına göre kullanıcıyı bulur (Yardımcı metod)
     *
     * @param username Kullanıcı adı
     * @return Bulunan kullanıcı
     * @throws UserNotFoundException Kullanıcı bulunamazsa
     */
    @Transactional(readOnly = true)
    public User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (user.isDeleted()) {
            throw new UserNotFoundException(username);
        }

        return user;
    }


    /**
     * @param user kullanıcı
     * @return kullanıcının rollerini döner
     */
    @Transactional(readOnly = true)
    public List<String> getRolesByUser(User user) {
        return user.getAuthorities().stream()
                .map(Role::getValue)
                .toList();
    }


    /**
     * @param email şifre sıfırlanmak istenen email
     * @return kullanıcı bilgileri
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Mail adresi bulunamadı."));
    }


    /**
     * Girilen şifrenin geçerli bir formatta olup olmadığını kontrol eder.
     *
     * @param password Şifre
     * @return Geçerli ise true, aksi halde false.
     */
    public Boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@#$%^&+=!~?<>|])(?=.{8,25}$).*$";
        return password.matches(passwordPattern);
    }


    /**
     * Kullanıcının rol güncellemesi sırasında, son adminin admin rolünü kaybetmesini engeller.
     *
     * @param existingUser   Güncellenmekte olan kullanıcı.
     * @param newAuthorities Yeni yetkiler.
     * @param userRepository Kullanıcı repository'si (admin sayısını kontrol etmek için).
     */
    public void validateAdminRoleChange(User existingUser, Set<Role> newAuthorities, UserRepository userRepository) {

        if (newAuthorities == null || Objects.equals(existingUser.getAuthorities(), newAuthorities)) {
            return;
        }

        boolean isCurrentUserAdmin = existingUser.getAuthorities().stream()
                .anyMatch(role -> role.getValue().equals(ROLE_ADMIN));

        if (!isCurrentUserAdmin) {
            throw new UnauthorizedAccessException("Bu işlem için yetkiniz yok.");
        }

        long adminCount = userRepository.countAdmins();

        boolean isLastAdmin = isCurrentUserAdmin && adminCount == 1 &&
                newAuthorities.stream().noneMatch(role -> role.getValue().equals(ROLE_ADMIN));

        if (isLastAdmin) {
            throw new UnauthorizedAccessException("Bu işlem için yetkiniz yok.");
        }
    }


}
