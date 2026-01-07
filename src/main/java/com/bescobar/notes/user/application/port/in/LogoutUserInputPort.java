package com.bescobar.notes.user.application.port.in;

public interface LogoutUserInputPort {
    void logout(String refreshToken);
}
