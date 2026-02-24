package com.bescobar.notes.user.application.port.out;

import com.bescobar.notes.user.domain.model.User;

public interface RefreshTokenRepositoryPort {
    String createRefreshToken(User user);

    User findUserByEmail(String email);

    void deleteRefreshToken(String refreshToken);

    void deleteAllRefreshTokensByUserId(Long userId);

    boolean refreshTokenExists(String refreshToken);
}
