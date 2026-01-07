package com.bescobar.notes.user.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.application.port.in.LogoutUserInputPort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;

import lombok.AllArgsConstructor;

/**
 * Service implementing the LogoutUserUseCase.
 * Handles user logout by invalidating refresh tokens.
 */
@Service
@AllArgsConstructor
public class LogoutUserUseCase implements LogoutUserInputPort {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepositoryPort.deleteRefreshToken(refreshToken);
    }
}
