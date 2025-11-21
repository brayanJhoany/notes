package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.application.port.in.RefreshTokenUseCase;
import com.bescobar.notes.user.application.port.out.JwtServicePort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;
import com.bescobar.notes.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the RefreshTokenUseCase.
 * Handles token refresh logic to generate new access and refresh tokens.
 */
@Service
@AllArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final JwtServicePort jwtServicePort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    @Transactional
    public AuthResponseDto refreshToken(String refreshToken) {
        String email;
        try {
            if (!jwtServicePort.isRefreshToken(refreshToken)) {
                throw new BadCredentialsException("Invalid token type");
            }

            email = jwtServicePort.extractEmail(refreshToken);

            if (jwtServicePort.isTokenExpired(refreshToken)) {
                throw new BadCredentialsException("Refresh token has expired");
            }
        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        User user = refreshTokenRepositoryPort.findUserByEmail(email);
        if (user == null) {
            throw new BadCredentialsException("User not found");
        }

        if (!refreshTokenRepositoryPort.refreshTokenExists(refreshToken)) {
            throw new BadCredentialsException("Token has been revoked");
        }

        // Delete all refresh tokens for this user to avoid unique constraint violations
        refreshTokenRepositoryPort.deleteAllRefreshTokensByUserId(user.getId());

        String accessToken = jwtServicePort.generateAccessToken(user.getEmail());
        String newRefreshToken = refreshTokenRepositoryPort.createRefreshToken(user);

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(newRefreshToken)
                .user(user)
                .build();
    }
}
