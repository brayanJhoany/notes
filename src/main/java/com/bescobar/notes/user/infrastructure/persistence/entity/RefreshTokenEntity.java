package com.bescobar.notes.user.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    private RefreshTokenEntity(Long id, String token, UserEntity user, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.token = token;
        this.user = copyUser(user);
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public UserEntity getUser() {
        return copyUser(user);
    }

    public void setUser(UserEntity user) {
        this.user = copyUser(user);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Defensive copy prevents callers from mutating the managed JPA instance.
    private static UserEntity copyUser(UserEntity source) {
        if (source == null) {
            return null;
        }

        UserEntity copy = new UserEntity();
        copy.setId(source.getId());
        copy.setFullName(source.getFullName());
        copy.setEmail(source.getEmail());
        copy.setPassword(source.getPassword());
        copy.setPhone(source.getPhone());
        copy.setAddress(source.getAddress());
        copy.setRole(source.getRole());
        copy.setActive(source.getActive());
        copy.setCreatedAt(source.getCreatedAt());
        copy.setUpdatedAt(source.getUpdatedAt());
        return copy;
    }

    public static class RefreshTokenEntityBuilder {
        public RefreshTokenEntityBuilder user(UserEntity user) {
            this.user = copyUser(user);
            return this;
        }
    }
}
