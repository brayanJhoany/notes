package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.dto.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserProfileService Unit Tests")
class UpdateUserProfileServiceTest {

    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private UpdateUserProfileService updateUserProfileService;

    private User existingUser;
    private UpdateProfileCommand updateCommand;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldusername");
        existingUser.setFullName("Old Name");
        existingUser.setEmail("old@example.com");
        existingUser.setPhone("+1111111111");
        existingUser.setAddress("Old Address");
        existingUser.setRole(Role.REGULAR);
        existingUser.setActive(true);

        updateCommand = new UpdateProfileCommand(
            "newusername",
            "New Name",
            "new@example.com",
            "+2222222222",
            "New Address"
        );
    }

    @Test
    @DisplayName("Should successfully update user profile")
    void shouldUpdateProfile() {
        // Given
        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        User updatedUser = updateUserProfileService.updateProfile(1L, updateCommand);

        // Then
        assertNotNull(updatedUser);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should update username, fullName, email, phone, and address")
    void shouldUpdateAllFields() {
        // Given
        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByEmail(updateCommand.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(updateCommand.getUsername())).thenReturn(false);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture())).thenReturn(existingUser);

        // When
        updateUserProfileService.updateProfile(1L, updateCommand);

        // Then
        User savedUser = userCaptor.getValue();
        assertEquals("newusername", savedUser.getUsername());
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
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

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
        when(userRepository.existsByUsername(anyString())).thenReturn(false);

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
        assertThrows(IllegalArgumentException.class, () -> {
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
        assertThrows(IllegalArgumentException.class, () -> {
            updateUserProfileService.updateProfile(1L, updateCommand);
        });

        verify(userRepository).existsByEmail(updateCommand.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when new username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(updateCommand.getUsername())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            updateUserProfileService.updateProfile(1L, updateCommand);
        });

        verify(userRepository).existsByUsername(updateCommand.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should allow keeping the same email")
    void shouldAllowKeepingSameEmail() {
        // Given
        UpdateProfileCommand sameEmailCommand = new UpdateProfileCommand(
            "newusername",
            "New Name",
            existingUser.getEmail(), // Same email
            "+2222222222",
            "New Address"
        );

        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        updateUserProfileService.updateProfile(1L, sameEmailCommand);

        // Then
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should allow keeping the same username")
    void shouldAllowKeepingSameUsername() {
        // Given
        UpdateProfileCommand sameUsernameCommand = new UpdateProfileCommand(
            existingUser.getUsername(), // Same username
            "New Name",
            "new@example.com",
            "+2222222222",
            "New Address"
        );

        when(userRepository.findById(1L)).thenReturn(existingUser);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        updateUserProfileService.updateProfile(1L, sameUsernameCommand);

        // Then
        verify(userRepository, never()).existsByUsername(anyString());
        verify(userRepository).save(any(User.class));
    }
}
