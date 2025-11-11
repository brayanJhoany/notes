package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.port.in.LogoutUserUseCase;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the LogoutUserUseCase.
 * Handles user logout by invalidating refresh tokens.
 */
@Service
@AllArgsConstructor
public class LogoutUserService implements LogoutUserUseCase {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepositoryPort.deleteRefreshToken(refreshToken);
    }
}
