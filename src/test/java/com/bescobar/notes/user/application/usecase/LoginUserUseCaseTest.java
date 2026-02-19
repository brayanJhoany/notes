package com.bescobar.notes.user.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bescobar.notes.user.application.port.in.query.AuthResponseDto;
import com.bescobar.notes.user.application.port.in.command.LoginCommand;
import com.bescobar.notes.user.application.port.out.JwtServicePort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.exception.UserAccountDisabledException;
import com.bescobar.notes.user.domain.exception.UserAuthenticationException;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUserUseCase Unit Tests")
class LoginUserUseCaseTest {

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtServicePort jwtService;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @InjectMocks
    private LoginUserUseCase loginUserService;

    private User testUser;
    private LoginCommand loginCommand;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(Role.REGULAR);
        testUser.setActive(true);

        loginCommand = new LoginCommand("test@example.com", "password123");
    }

    @Test
    @DisplayName("Should successfully login with valid credentials")
    void shouldLoginWithValidCredentials() {
        // Given
        when(userRepository.findByEmail(loginCommand.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches(loginCommand.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(testUser.getEmail())).thenReturn("access-token");
        when(refreshTokenRepository.createRefreshToken(testUser)).thenReturn("refresh-token");

        // When
        AuthResponseDto response = loginUserService.login(loginCommand);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(testUser, response.getUser());

        verify(userRepository).findByEmail(loginCommand.getEmail());
        verify(passwordEncoder).matches(loginCommand.getPassword(), testUser.getPassword());
        verify(jwtService).generateAccessToken(testUser.getEmail());
        verify(refreshTokenRepository).createRefreshToken(testUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            loginUserService.login(loginCommand);
        });

        verify(userRepository).findByEmail(loginCommand.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateAccessToken(anyString());
    }

    @Test
    @DisplayName("Should throw exception when user is inactive")
    void shouldThrowExceptionWhenUserInactive() {
        // Given
        testUser.setActive(false);
        when(userRepository.findByEmail(loginCommand.getEmail())).thenReturn(testUser);

        // When & Then
        assertThrows(UserAccountDisabledException.class, () -> {
            loginUserService.login(loginCommand);
        });

        verify(userRepository).findByEmail(loginCommand.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void shouldThrowExceptionWhenPasswordIncorrect() {
        // Given
        when(userRepository.findByEmail(loginCommand.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches(loginCommand.getPassword(), testUser.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(UserAuthenticationException.class, () -> {
            loginUserService.login(loginCommand);
        });

        verify(userRepository).findByEmail(loginCommand.getEmail());
        verify(passwordEncoder).matches(loginCommand.getPassword(), testUser.getPassword());
        verify(jwtService, never()).generateAccessToken(anyString());
    }

    @Test
    @DisplayName("Should generate new tokens on successful login")
    void shouldGenerateNewTokens() {
        // Given
        when(userRepository.findByEmail(loginCommand.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches(loginCommand.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(testUser.getEmail())).thenReturn("new-access-token");
        when(refreshTokenRepository.createRefreshToken(testUser)).thenReturn("new-refresh-token");

        // When
        AuthResponseDto response = loginUserService.login(loginCommand);

        // Then
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
    }

    @Test
    @DisplayName("Should return user information in response")
    void shouldReturnUserInformation() {
        // Given
        when(userRepository.findByEmail(loginCommand.getEmail())).thenReturn(testUser);
        when(passwordEncoder.matches(loginCommand.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(anyString())).thenReturn("access-token");
        when(refreshTokenRepository.createRefreshToken(any(User.class))).thenReturn("refresh-token");

        // When
        AuthResponseDto response = loginUserService.login(loginCommand);

        // Then
        assertNotNull(response.getUser());
        assertEquals(testUser.getId(), response.getUser().getId());
        assertEquals(testUser.getEmail(), response.getUser().getEmail());
    }
}
