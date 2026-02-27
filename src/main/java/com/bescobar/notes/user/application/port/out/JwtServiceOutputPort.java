package com.bescobar.notes.user.application.port.out;

public interface JwtServiceOutputPort {
    String generateAccessToken(String email);

    String generateRefreshToken(String email, Long userId);

    Long getAccessTokenExpiration();

    Long getRefreshTokenExpiration();

    boolean isTokenExpired(String token);

    String extractEmail(String token);

    boolean isRefreshToken(String token);

    boolean isTokenValid(String token, String email);
}
