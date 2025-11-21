package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.application.port.out.JwtServicePort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService Unit Tests")
class RefreshTokenServiceTest {

    @Mock
    private JwtServicePort jwtService;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User testUser;
    private String validRefreshToken;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        testUser.setRole(Role.REGULAR);
        testUser.setActive(true);

        validRefreshToken = "valid.refresh.token";
    }

    @Test
    @DisplayName("Should successfully refresh tokens with valid refresh token")
    void shouldRefreshTokensSuccessfully() {
        // Given
        when(jwtService.isRefreshToken(validRefreshToken)).thenReturn(true);
        when(jwtService.extractEmail(validRefreshToken)).thenReturn(testUser.getEmail());
        when(jwtService.isTokenExpired(validRefreshToken)).thenReturn(false);
        when(refreshTokenRepository.refreshTokenExists(validRefreshToken)).thenReturn(true);
        when(refreshTokenRepository.findUserByEmail(testUser.getEmail())).thenReturn(testUser);
        when(jwtService.generateAccessToken(testUser.getEmail())).thenReturn("new-access-token");
        when(refreshTokenRepository.createRefreshToken(testUser)).thenReturn("new-refresh-token");

        // When
        AuthResponseDto response = refreshTokenService.refreshToken(validRefreshToken);

        // Then
        assertNotNull(response);
        assertEquals("new-access-token", response.getToken());
        assertEquals("new-refresh-token", response.getRefreshToken());
        assertEquals(testUser, response.getUser());

        verify(jwtService).isRefreshToken(validRefreshToken);
        verify(jwtService).extractEmail(validRefreshToken);
        verify(jwtService).isTokenExpired(validRefreshToken);
        verify(refreshTokenRepository).refreshTokenExists(validRefreshToken);
        verify(refreshTokenRepository).deleteAllRefreshTokensByUserId(testUser.getId());
        verify(refreshTokenRepository).createRefreshToken(testUser);
    }

    @Test
    @DisplayName("Should throw exception when token is not a refresh token")
    void shouldThrowExceptionWhenNotRefreshToken() {
        // Given
        when(jwtService.isRefreshToken(anyString())).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            refreshTokenService.refreshToken("access.token");
        });

        verify(jwtService).isRefreshToken("access.token");
        verify(jwtService, never()).extractEmail(anyString());
        verify(refreshTokenRepository, never()).deleteRefreshToken(anyString());
    }

    @Test
    @DisplayName("Should throw exception when refresh token is expired")
    void shouldThrowExceptionWhenTokenExpired() {
        // Given
        when(jwtService.isRefreshToken(validRefreshToken)).thenReturn(true);
        when(jwtService.extractEmail(validRefreshToken)).thenReturn(testUser.getEmail());
        when(jwtService.isTokenExpired(validRefreshToken)).thenReturn(true);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            refreshTokenService.refreshToken(validRefreshToken);
        });

        verify(jwtService).isTokenExpired(validRefreshToken);
        verify(refreshTokenRepository, never()).refreshTokenExists(anyString());
    }

    @Test
    @DisplayName("Should throw exception when refresh token not found in database")
    void shouldThrowExceptionWhenTokenNotInDatabase() {
        // Given
        when(jwtService.isRefreshToken(validRefreshToken)).thenReturn(true);
        when(jwtService.extractEmail(validRefreshToken)).thenReturn(testUser.getEmail());
        when(jwtService.isTokenExpired(validRefreshToken)).thenReturn(false);
        when(refreshTokenRepository.findUserByEmail(testUser.getEmail())).thenReturn(testUser);
        when(refreshTokenRepository.refreshTokenExists(validRefreshToken)).thenReturn(false);

        // When & Then
        assertThrows(BadCredentialsException.class, () -> {
            refreshTokenService.refreshToken(validRefreshToken);
        });

        verify(refreshTokenRepository).findUserByEmail(testUser.getEmail());
        verify(refreshTokenRepository).refreshTokenExists(validRefreshToken);
        verify(refreshTokenRepository, never()).deleteRefreshToken(anyString());
    }

    @Test
    @DisplayName("Should delete old refresh token before creating new one")
    void shouldDeleteOldToken() {
        // Given
        when(jwtService.isRefreshToken(validRefreshToken)).thenReturn(true);
        when(jwtService.extractEmail(validRefreshToken)).thenReturn(testUser.getEmail());
        when(jwtService.isTokenExpired(validRefreshToken)).thenReturn(false);
        when(refreshTokenRepository.findUserByEmail(testUser.getEmail())).thenReturn(testUser);
        when(refreshTokenRepository.refreshTokenExists(validRefreshToken)).thenReturn(true);
        when(jwtService.generateAccessToken(anyString())).thenReturn("new-access-token");
        when(refreshTokenRepository.createRefreshToken(any(User.class))).thenReturn("new-refresh-token");

        // When
        refreshTokenService.refreshToken(validRefreshToken);

        // Then
        var inOrder = inOrder(refreshTokenRepository);
        inOrder.verify(refreshTokenRepository).deleteAllRefreshTokensByUserId(testUser.getId());
        inOrder.verify(refreshTokenRepository).createRefreshToken(testUser);
    }

    @Test
    @DisplayName("Should generate both access and refresh tokens")
    void shouldGenerateBothTokens() {
        // Given
        when(jwtService.isRefreshToken(validRefreshToken)).thenReturn(true);
        when(jwtService.extractEmail(validRefreshToken)).thenReturn(testUser.getEmail());
        when(jwtService.isTokenExpired(validRefreshToken)).thenReturn(false);
        when(refreshTokenRepository.refreshTokenExists(validRefreshToken)).thenReturn(true);
        when(refreshTokenRepository.findUserByEmail(testUser.getEmail())).thenReturn(testUser);
        when(jwtService.generateAccessToken(testUser.getEmail())).thenReturn("new-access-token");
        when(refreshTokenRepository.createRefreshToken(testUser)).thenReturn("new-refresh-token");

        // When
        AuthResponseDto response = refreshTokenService.refreshToken(validRefreshToken);

        // Then
        assertNotNull(response.getToken());
        assertNotNull(response.getRefreshToken());
        verify(jwtService).generateAccessToken(testUser.getEmail());
        verify(refreshTokenRepository).createRefreshToken(testUser);
    }
}
