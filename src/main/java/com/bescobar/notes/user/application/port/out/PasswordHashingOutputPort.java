package com.bescobar.notes.user.application.port.out;

public interface PasswordHashingOutputPort {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
