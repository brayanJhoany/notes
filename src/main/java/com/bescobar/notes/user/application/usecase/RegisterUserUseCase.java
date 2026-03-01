package com.bescobar.notes.user.application.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.application.port.in.query.AuthResponseDto;
import com.bescobar.notes.user.application.port.in.RegisterUserInputPort;
import com.bescobar.notes.user.application.port.out.JwtServiceOutputPort;
import com.bescobar.notes.user.application.port.out.PasswordHashingOutputPort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryOutputPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.exception.UserAlreadyExistsException;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;

/**
 * Service implementing the RegisterUserUseCase.
 * Handles user registration with password encryption and token generation.
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class RegisterUserUseCase implements RegisterUserInputPort {

    private final UserRepositoryOutputPort userRepositoryPort;
    private final PasswordHashingOutputPort passwordHashingOutputPort;
    private final JwtServiceOutputPort jwtServicePort;
    private final RefreshTokenRepositoryOutputPort refreshTokenRepositoryPort;

    @Override
    @Transactional
    public AuthResponseDto registerUser(User user) {
        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException(user.getEmail());
        }

        user.setPassword(passwordHashingOutputPort.encode(user.getPassword()));
        user.setRole(user.getRole() != null ? user.getRole() : Role.REGULAR);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepositoryPort.save(user);
        String accessToken = jwtServicePort.generateAccessToken(savedUser.getEmail());
        String refreshToken = refreshTokenRepositoryPort.createRefreshToken(savedUser);
        Long expiresIn = jwtServicePort.getAccessTokenExpiration() / 1000; // Convert ms to seconds

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .refreshToken(refreshToken)
                .user(savedUser)
                .build();
    }
}
