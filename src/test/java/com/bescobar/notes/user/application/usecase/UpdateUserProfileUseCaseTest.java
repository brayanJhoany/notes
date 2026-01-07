package com.bescobar.notes.user.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bescobar.notes.user.application.dto.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.exception.UserAlreadyExistsException;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserProfileUseCase Unit Tests")
class UpdateUserProfileUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private UpdateUserProfileUseCase updateUserProfileService;

    private User existingUser;
    private UpdateProfileCommand updateCommand;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFullName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setPhone("+1111111111");
        existingUser.setAddress("Old Address");
        existingUser.setRole(Role.REGULAR);
        existingUser.setActive(true);

        updateCommand = UpdateProfileCommand.builder()
            .fullName("New Name")
            .email("new@example.com")
            .phone("+2222222222")
            .address("New Address")
            .build();
    }

    @Test
    @DisplayName("Should successfully update user profile")
    void shouldUpdateProfile() {
        // Given
        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        User updatedUser = updateUserProfileService.updateProfile(1L, updateCommand);

        // Then
        assertNotNull(updatedUser);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should update fullName, email, phone, and address")
    void shouldUpdateAllFields() {
        // Given
        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByEmail(updateCommand.getEmail())).thenReturn(false);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(existingUser);

        // When
        updateUserProfileService.updateProfile(1L, updateCommand);

        // Then
        User savedUser = userCaptor.getValue();
        assertEquals("New Name", savedUser.getFullName());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("+2222222222", savedUser.getPhone());
        assertEquals("New Address", savedUser.getAddress());
    }

    @Test
    @DisplayName("Should not update password, role, active, or createdAt")
    void shouldNotUpdateProtectedFields() {
        // Given
        String originalPassword = "originalPassword";
        existingUser.setPassword(originalPassword);

        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(existingUser);

        // When
        updateUserProfileService.updateProfile(1L, updateCommand);

        // Then
        User savedUser = userCaptor.getValue();
        assertEquals(originalPassword, savedUser.getPassword());
        assertEquals(Role.REGULAR, savedUser.getRole());
        assertTrue(savedUser.getActive());
    }

    @Test
    @DisplayName("Should update updatedAt timestamp")
    void shouldUpdateTimestamp() {
        // Given
        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(existingUser);

        // When
        updateUserProfileService.updateProfile(1L, updateCommand);

        // Then
        assertNotNull(userCaptor.getValue().getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(null);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            updateUserProfileService.updateProfile(1L, updateCommand);
        });

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when new email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByEmail(updateCommand.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> {
            updateUserProfileService.updateProfile(1L, updateCommand);
        });

        verify(userRepository).existsByEmail(updateCommand.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should allow keeping the same email")
    void shouldAllowKeepingSameEmail() {
        // Given
        UpdateProfileCommand sameEmailCommand = UpdateProfileCommand.builder()
            .fullName("New Name")
            .email(existingUser.getEmail())
            .phone("+2222222222")
            .address("New Address")
            .build();

        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        updateUserProfileService.updateProfile(1L, sameEmailCommand);

        // Then
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

}
