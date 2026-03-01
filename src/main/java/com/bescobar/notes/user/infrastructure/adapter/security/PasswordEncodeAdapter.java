package com.bescobar.notes.user.infrastructure.adapter.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bescobar.notes.user.application.port.out.PasswordHashingOutputPort;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PasswordEncodeAdapter implements PasswordHashingOutputPort {
    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
