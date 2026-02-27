package com.bescobar.notes.user.application.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.repository.RefreshTokenJpaRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUserUseCase Unit Tests")
class DeleteUserUseCaseTest {

    @Mock
    private UserRepositoryOutputPort userRepositoryPort;

    @Mock
    private RefreshTokenJpaRepository refreshTokenRepository;

    @InjectMocks
    private DeleteUserUseCase deleteUserUseCase;

    @Test
    @DisplayName("Should delete refresh tokens then delete user")
    void shouldDeleteRefreshTokensThenUser() {
        User user = new User();
        user.setId(1L);
        when(userRepositoryPort.findById(1L)).thenReturn(user);

        deleteUserUseCase.deleteUser(1L);

        var inOrder = inOrder(refreshTokenRepository, userRepositoryPort);
        inOrder.verify(refreshTokenRepository).deleteByUserId(1L);
        inOrder.verify(userRepositoryPort).deleteById(1L);
        verify(userRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user does not exist")
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepositoryPort.findById(99L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> deleteUserUseCase.deleteUser(99L));

        verify(userRepositoryPort).findById(99L);
        verify(refreshTokenRepository, never()).deleteByUserId(99L);
        verify(userRepositoryPort, never()).deleteById(99L);
    }
}

