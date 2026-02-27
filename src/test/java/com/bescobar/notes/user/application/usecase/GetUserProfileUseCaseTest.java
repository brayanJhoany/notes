package com.bescobar.notes.user.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserProfileUseCase Unit Tests")
class GetUserProfileUseCaseTest {

    @Mock
    private UserRepositoryOutputPort userRepositoryPort;

    @InjectMocks
    private GetUserProfileUseCase getUserProfileUseCase;

    @Test
    @DisplayName("Should return profile by id when user exists")
    void shouldReturnProfileById() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        when(userRepositoryPort.findById(1L)).thenReturn(user);

        User result = getUserProfileUseCase.getProfileById(1L);

        assertEquals(1L, result.getId());
        verify(userRepositoryPort).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user id does not exist")
    void shouldThrowWhenUserIdDoesNotExist() {
        when(userRepositoryPort.findById(99L)).thenReturn(null);

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> getUserProfileUseCase.getProfileById(99L));
        assertTrue(ex.getMessage().contains("99"));
        verify(userRepositoryPort).findById(99L);
    }

    @Test
    @DisplayName("Should return profile by email when user exists")
    void shouldReturnProfileByEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        when(userRepositoryPort.findByEmail("test@example.com")).thenReturn(user);

        User result = getUserProfileUseCase.getProfileByEmail("test@example.com");

        assertEquals("test@example.com", result.getEmail());
        verify(userRepositoryPort).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should throw exception when user email does not exist")
    void shouldThrowWhenUserEmailDoesNotExist() {
        when(userRepositoryPort.findByEmail("missing@example.com")).thenReturn(null);

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> getUserProfileUseCase.getProfileByEmail("missing@example.com"));
        assertTrue(ex.getMessage().contains("missing@example.com"));
        verify(userRepositoryPort).findByEmail("missing@example.com");
    }
}

