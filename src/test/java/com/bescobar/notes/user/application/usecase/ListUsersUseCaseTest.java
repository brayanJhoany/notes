package com.bescobar.notes.user.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListUsersUseCase Unit Tests")
class ListUsersUseCaseTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private ListUsersUseCase listUsersUseCase;

    @Test
    @DisplayName("Should delegate user listing to repository port")
    void shouldDelegateToRepositoryPort() {
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Page<User> expected = new PageImpl<>(List.of(user), pageable, 1);
        when(userRepositoryPort.findAll(pageable, "test", "name")).thenReturn(expected);

        Page<User> result = listUsersUseCase.getAllUsers(pageable, "test", "name");

        assertEquals(1, result.getTotalElements());
        assertEquals("test@example.com", result.getContent().get(0).getEmail());
        verify(userRepositoryPort).findAll(pageable, "test", "name");
    }
}
