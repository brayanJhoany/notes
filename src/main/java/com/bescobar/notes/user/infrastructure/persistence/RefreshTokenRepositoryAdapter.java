package com.bescobar.notes.user.infrastructure.persistence;

import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;
import com.bescobar.notes.user.application.port.out.JwtServicePort;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.entity.RefreshTokenEntity;
import com.bescobar.notes.user.infrastructure.persistence.mapper.UserMapper;
import com.bescobar.notes.user.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import com.bescobar.notes.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository refreshTokenRepository;
    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;
    private final JwtServicePort jwtService;

    @Override
    public String createRefreshToken(User user) {
        // Generate JWT refresh token with email included
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), user.getId());

        // Store in database for validation
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .token(refreshToken)
                .user(userMapper.toEntity(user))
                .expiresAt(java.time.LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpiration() / 1000))
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
    public boolean refreshTokenExists(String refreshToken) {
        return refreshTokenRepository.existsByToken(refreshToken);
    }
}