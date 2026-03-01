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

import com.bescobar.notes.user.application.port.in.query.AuthResponseDto;
import com.bescobar.notes.user.application.port.out.JwtServiceOutputPort;
import com.bescobar.notes.user.application.port.out.PasswordHashingOutputPort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryOutputPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.exception.UserAlreadyExistsException;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterUserUseCase Unit Tests")
class RegisterUserUseCaseTest {

    @Mock
    private UserRepositoryOutputPort userRepository;

    @Mock
    private PasswordHashingOutputPort passwordHashingOutputPort;

    @Mock
    private JwtServiceOutputPort jwtService;

    @Mock
    private RefreshTokenRepositoryOutputPort refreshTokenRepository;

    @InjectMocks
    private RegisterUserUseCase registerUserService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFullName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setPhone("+1234567890");
        testUser.setAddress("123 Test St");
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void shouldRegisterNewUser() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordHashingOutputPort.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail(testUser.getEmail());
        savedUser.setFullName(testUser.getFullName());
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.REGULAR);
        savedUser.setActive(true);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(anyString())).thenReturn("access-token");
        when(refreshTokenRepository.createRefreshToken(any(User.class))).thenReturn("refresh-token");

        // When
        AuthResponseDto response = registerUserService.registerUser(testUser);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertNotNull(response.getUser());
        assertEquals(testUser.getEmail(), response.getUser().getEmail());

        // Verify interactions
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(passwordHashingOutputPort).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateAccessToken(testUser.getEmail());
        verify(refreshTokenRepository).createRefreshToken(savedUser);
    }

    @Test
    @DisplayName("Should encode password before saving")
    void shouldEncodePassword() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordHashingOutputPort.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(anyString())).thenReturn("access-token");
        when(refreshTokenRepository.createRefreshToken(any(User.class))).thenReturn("refresh-token");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        registerUserService.registerUser(testUser);

        // Then
        verify(userRepository).save(userCaptor.capture());
        assertEquals("encodedPassword", userCaptor.getValue().getPassword());
    }

    @Test
    @DisplayName("Should set default role to REGULAR")
    void shouldSetDefaultRole() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordHashingOutputPort.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(anyString())).thenReturn("access-token");
        when(refreshTokenRepository.createRefreshToken(any(User.class))).thenReturn("refresh-token");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        registerUserService.registerUser(testUser);

        // Then
        verify(userRepository).save(userCaptor.capture());
        assertEquals(Role.REGULAR, userCaptor.getValue().getRole());
    }

    @Test
    @DisplayName("Should set active status to true by default")
    void shouldSetActiveStatus() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordHashingOutputPort.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(anyString())).thenReturn("access-token");
        when(refreshTokenRepository.createRefreshToken(any(User.class))).thenReturn("refresh-token");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        registerUserService.registerUser(testUser);

        // Then
        verify(userRepository).save(userCaptor.capture());
        assertTrue(userCaptor.getValue().getActive());
    }

    @Test
    @DisplayName("Should set createdAt and updatedAt timestamps")
    void shouldSetTimestamps() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordHashingOutputPort.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(anyString())).thenReturn("access-token");
        when(refreshTokenRepository.createRefreshToken(any(User.class))).thenReturn("refresh-token");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // When
        registerUserService.registerUser(testUser);

        // Then
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue().getCreatedAt());
        assertNotNull(userCaptor.getValue().getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> {
            registerUserService.registerUser(testUser);
        });

        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should generate JWT tokens after successful registration")
    void shouldGenerateTokens() {
        // Given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordHashingOutputPort.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateAccessToken(anyString())).thenReturn("access-token");
        when(refreshTokenRepository.createRefreshToken(any(User.class))).thenReturn("refresh-token");

        // When
        AuthResponseDto response = registerUserService.registerUser(testUser);

        // Then
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        verify(jwtService).generateAccessToken(testUser.getEmail());
        verify(refreshTokenRepository).createRefreshToken(any(User.class));
    }
}
