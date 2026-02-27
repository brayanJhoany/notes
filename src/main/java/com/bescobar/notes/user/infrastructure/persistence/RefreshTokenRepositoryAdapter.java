package com.bescobar.notes.user.infrastructure.persistence;

import java.time.LocalDateTime;

import com.bescobar.notes.user.application.port.out.JwtServiceOutputPort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryOutputPort;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.entity.RefreshTokenEntity;
import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;
import com.bescobar.notes.user.infrastructure.persistence.mapper.UserMapper;
import com.bescobar.notes.user.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.bescobar.notes.user.infrastructure.persistence.repository.UserJpaRepository;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryOutputPort {

    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;
    private final JwtServiceOutputPort jwtService;
    private final EntityManager entityManager;

    @Override
    public String createRefreshToken(User user) {
        // Generate JWT refresh token with email included
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId());

        UserEntity userEntity = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Store in database for validation
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .token(refreshToken)
                .user(userEntity)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpiration() / 1000))
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
        return refreshToken;
    }

    @Override
    public User findUserByEmail(String email) {
        var userEntity = userRepository.findByEmail(email);
        return userMapper.toDomain(userEntity);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Override
    public void deleteAllRefreshTokensByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        // Force immediate execution of the delete before subsequent operations
        entityManager.flush();
    }

    @Override
    public boolean refreshTokenExists(String refreshToken) {
        return refreshTokenRepository.existsByToken(refreshToken);
    }
}
