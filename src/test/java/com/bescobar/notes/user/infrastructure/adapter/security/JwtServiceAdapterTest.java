package com.bescobar.notes.user.infrastructure.adapter.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtServiceAdapter Unit Tests")
class JwtServiceAdapterTest {

    private JwtServiceAdapter jwtServiceAdapter;
    private final String testEmail = "test@example.com";
    private final Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        jwtServiceAdapter = new JwtServiceAdapter();

        // Set test configuration values
        String secretKey = Base64.getEncoder().encodeToString("test-secret-key-for-jwt-token-generation-min-256-bits".getBytes());
        ReflectionTestUtils.setField(jwtServiceAdapter, "secret", secretKey);
        ReflectionTestUtils.setField(jwtServiceAdapter, "accessTokenExpiration", 1800000L); // 30 minutes
        ReflectionTestUtils.setField(jwtServiceAdapter, "refreshTokenExpiration", 604800000L); // 7 days
    }

    @Test
    @DisplayName("Should generate access token")
    void shouldGenerateAccessToken() {
        // When
        String token = jwtServiceAdapter.generateAccessToken(testEmail);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    @DisplayName("Should generate refresh token")
    void shouldGenerateRefreshToken() {
        // When
        String token = jwtServiceAdapter.generateRefreshToken(testEmail, testUserId);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    @DisplayName("Should extract email from token")
    void shouldExtractEmail() {
        // Given
        String token = jwtServiceAdapter.generateAccessToken(testEmail);

        // When
        String extractedEmail = jwtServiceAdapter.extractEmail(token);

        // Then
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    @DisplayName("Should validate token is not expired")
    void shouldValidateTokenNotExpired() {
        // Given
        String token = jwtServiceAdapter.generateAccessToken(testEmail);

        // When
        boolean isExpired = jwtServiceAdapter.isTokenExpired(token);

        // Then
        assertFalse(isExpired);
    }

    @Test
    @DisplayName("Should identify refresh token correctly")
    void shouldIdentifyRefreshToken() {
        // Given
        String refreshToken = jwtServiceAdapter.generateRefreshToken(testEmail, testUserId);
        String accessToken = jwtServiceAdapter.generateAccessToken(testEmail);

        // When & Then
        assertTrue(jwtServiceAdapter.isRefreshToken(refreshToken));
        assertFalse(jwtServiceAdapter.isRefreshToken(accessToken));
    }

    @Test
    @DisplayName("Should validate token with correct email")
    void shouldValidateTokenWithCorrectEmail() {
        // Given
        String token = jwtServiceAdapter.generateAccessToken(testEmail);

        // When
        boolean isValid = jwtServiceAdapter.isTokenValid(token, testEmail);

        // Then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should invalidate token with incorrect email")
    void shouldInvalidateTokenWithIncorrectEmail() {
        // Given
        String token = jwtServiceAdapter.generateAccessToken(testEmail);

        // When
        boolean isValid = jwtServiceAdapter.isTokenValid(token, "wrong@example.com");

        // Then
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should get refresh token expiration time")
    void shouldGetRefreshTokenExpiration() {
        // When
        Long expiration = jwtServiceAdapter.getRefreshTokenExpiration();

        // Then
        assertNotNull(expiration);
        assertEquals(604800000L, expiration); // 7 days in milliseconds
    }

    @Test
    @DisplayName("Should generate different tokens for different emails")
    void shouldGenerateDifferentTokensForDifferentEmails() {
        // Given
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        // When
        String token1 = jwtServiceAdapter.generateAccessToken(email1);
        String token2 = jwtServiceAdapter.generateAccessToken(email2);

        // Then
        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should handle invalid token gracefully")
    void shouldHandleInvalidToken() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> {
            jwtServiceAdapter.extractEmail(invalidToken);
        });
    }

    @Test
    @DisplayName("Access token and refresh token should be different")
    void accessAndRefreshTokensShouldBeDifferent() {
        // When
        String accessToken = jwtServiceAdapter.generateAccessToken(testEmail);
        String refreshToken = jwtServiceAdapter.generateRefreshToken(testEmail, testUserId);

        // Then
        assertNotEquals(accessToken, refreshToken);
    }
}
