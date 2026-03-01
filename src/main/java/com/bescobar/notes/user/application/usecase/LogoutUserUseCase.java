package com.bescobar.notes.user.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.application.port.in.LogoutUserInputPort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryOutputPort;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;

/**
 * Service implementing the LogoutUserUseCase.
 * Handles user logout by invalidating refresh tokens.
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class LogoutUserUseCase implements LogoutUserInputPort {

    private final RefreshTokenRepositoryOutputPort refreshTokenRepositoryPort;

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepositoryPort.deleteRefreshToken(refreshToken);
    }
}
