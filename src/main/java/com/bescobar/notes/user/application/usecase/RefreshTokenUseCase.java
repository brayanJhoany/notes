package com.bescobar.notes.user.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.application.port.in.RefreshTokenInputPort;
import com.bescobar.notes.user.application.port.out.JwtServicePort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;
import com.bescobar.notes.user.domain.exception.UserAuthenticationException;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Service implementing the RefreshTokenUseCase. Handles token refresh logic to
 * generate new access and refresh tokens.
 */
@Service
@AllArgsConstructor
public class RefreshTokenUseCase implements RefreshTokenInputPort {

    private final JwtServicePort jwtServicePort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    @Transactional
    public AuthResponseDto refreshToken(String refreshToken) {
        String email;
        try {
            if (!jwtServicePort.isRefreshToken(refreshToken)) {
                throw new UserAuthenticationException("Invalid token type");
            }
            if (jwtServicePort.isTokenExpired(refreshToken)) {
                throw new UserAuthenticationException("Refresh token has expired");
            }
            email = jwtServicePort.extractEmail(refreshToken);
        } catch (UserAuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UserAuthenticationException("Invalid or expired refresh token");
        }

        User user = refreshTokenRepositoryPort.findUserByEmail(email);
        if (user == null) {
            throw new UserAuthenticationException("Invalid or expired refresh token");
        }

        if (!refreshTokenRepositoryPort.refreshTokenExists(refreshToken)) {
            throw new UserAuthenticationException("Token has been revoked");
        }
        refreshTokenRepositoryPort.deleteAllRefreshTokensByUserId(user.getId());
        String accessToken = jwtServicePort.generateAccessToken(user.getEmail());
        String newRefreshToken = refreshTokenRepositoryPort.createRefreshToken(user);
        Long expiresIn = jwtServicePort.getAccessTokenExpiration() / 1000; // Convert ms to seconds

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .refreshToken(newRefreshToken)
                .user(user)
                .build();
    }
}
