package com.bescobar.notes.user.application.usecase;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryOutputPort;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogoutUserUseCase Unit Tests")
class LogoutUserUseCaseTest {

    @Mock
    private RefreshTokenRepositoryOutputPort refreshTokenRepositoryPort;

    @InjectMocks
    private LogoutUserUseCase logoutUserUseCase;

    @Test
    @DisplayName("Should delete refresh token on logout")
    void shouldDeleteRefreshTokenOnLogout() {
        logoutUserUseCase.logout("refresh.token");
        verify(refreshTokenRepositoryPort).deleteRefreshToken("refresh.token");
    }
}

