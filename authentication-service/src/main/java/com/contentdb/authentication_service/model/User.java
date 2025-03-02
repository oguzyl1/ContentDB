package com.contentdb.authentication_service.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email")})
public class User implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String id;

    @NotBlank(message = "İsim boş bırakılamaz")
    @Size(min = 2, max = 20, message = "isim 2 ila 20 karakter arasında olmalıdır!")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Soyisim boş bırakılamaz")
    @Size(min = 2, max = 20, message = "Soyisim 2 ile 50 karakter arasında olmalıdır")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Email(message = "Geçersiz e-posta formatı")
    @NotBlank(message = "E-posta boş bırakılamaz")
    @Column(nullable = false)
    private String email;

    @NotBlank(message = "Kullanıcı adı boş bırakılamaz")
    @Size(min = 3, max = 15, message = "Kullanıcı adı 3 ile 15 karakter arasında olmalıdır")
    @Column(nullable = false)
    private String username;

    @NotBlank(message = "Şifre boş bırakılamaz")
    @Size(min = 8, max = 50, message = "Şifre en az 8 karakter olmalıdır")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Yetki boş bırakılamaz")
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "authorities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Role> authorities = new HashSet<>();

    private boolean accountNonExpired = true;
    private boolean isEnabled = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;

    @Column(nullable = false)
    private boolean isDeleted = false;

    private LocalDateTime lastLoginTime;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;
    private LocalDateTime passwordChangedAt;
    private LocalDateTime resetTokenCreatedAt;

    public User() {
    }

    public User(String id, String name, String lastName, String email, String username, String password, Set<Role> authorities, boolean accountNonExpired, boolean isEnabled, boolean accountNonLocked, boolean credentialsNonExpired, boolean isDeleted, LocalDateTime lastLoginTime, LocalDateTime createdAt, LocalDateTime deletedAt, LocalDateTime passwordChangedAt, LocalDateTime resetTokenCreatedAt) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.accountNonExpired = accountNonExpired;
        this.isEnabled = isEnabled;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.isDeleted = isDeleted;
        this.lastLoginTime = lastLoginTime;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.passwordChangedAt = passwordChangedAt;
        this.resetTokenCreatedAt = resetTokenCreatedAt;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public Set<Role> getAuthorities() {
        return new HashSet<>(authorities);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public LocalDateTime getResetTokenCreatedAt() {
        return resetTokenCreatedAt;
    }

    public void setResetTokenCreatedAt(LocalDateTime resetTokenCreatedAt) {
        this.resetTokenCreatedAt = resetTokenCreatedAt;
    }

    public LocalDateTime getPasswordChangedAt() {
        return passwordChangedAt;
    }

    public void setPasswordChangedAt(LocalDateTime passwordChangedAt) {
        this.passwordChangedAt = passwordChangedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities != null ? new HashSet<>(authorities) : new HashSet<>();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email);
    }

    @Override
    public String toString() {
        return "User{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", lastName='" + lastName + '\'' + ", email='" + email + '\'' + ", username='" + username + '\'' + "}";
    }

    public static class Builder {
        private String id;
        private String name;
        private String lastName;
        private String email;
        private String username;
        private String password;
        private Set<Role> authorities = new HashSet<>();
        private boolean accountNonExpired = true;
        private boolean isEnabled = true;
        private boolean accountNonLocked = true;
        private boolean credentialsNonExpired = true;


        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder accountNonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        public Builder isEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
            return this;
        }

        public Builder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public Builder credentialsNonExpired(boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        public Builder authorities(Set<Role> authorities) {
            this.authorities = authorities != null ? new HashSet<>(authorities) : new HashSet<>();
            return this;
        }

        public User build() {
            User user = new User();
            user.setId(this.id);
            user.setName(this.name);
            user.setLastName(this.lastName);
            user.setEmail(this.email);
            user.setUsername(this.username);
            user.setPassword(this.password);
            user.setAccountNonExpired(this.accountNonExpired);
            user.setEnabled(this.isEnabled);
            user.setAccountNonLocked(this.accountNonLocked);
            user.setCredentialsNonExpired(this.credentialsNonExpired);
            user.setAuthorities(this.authorities);
            return user;
        }
    }
}
