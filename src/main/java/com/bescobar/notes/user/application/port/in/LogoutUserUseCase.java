package com.bescobar.notes.user.application.port.in;

public interface LogoutUserUseCase {
    void logout(String refreshToken);
}
