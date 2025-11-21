package com.bescobar.notes.user.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "The username is required.")
    @Size(min = 3, max = 255)
    private String username;

    @Column(nullable = false, length = 255, name = "full_name")
    @NotBlank(message = "The full name is required")
    private String fullName;

    @Column(nullable = false, unique = true, length = 255)
    @Email
    @NotBlank(message = "The email is required")
    private String email;

    @Column(nullable = false, length = 255)
    @JsonIgnore
    @NotBlank(message = "The password is required")
    private String password;

    @Column(nullable = false, length = 30)
    @NotBlank(message = "The phone is required")
    private String phone;

    @Column(nullable = false, length = 255)
    @NotBlank(message = "The address is required")
    private String address;

    @Column(nullable = false, length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'REGULAR'")
    @Enumerated(EnumType.STRING)
    private RoleEntity role = RoleEntity.REGULAR;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private Boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RefreshTokenEntity> refreshTokens;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

}
