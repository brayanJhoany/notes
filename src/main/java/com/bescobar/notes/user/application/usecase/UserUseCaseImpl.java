package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.application.dto.LoginCommand;
import com.bescobar.notes.user.application.dto.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.in.UserUseCase;
import com.bescobar.notes.user.application.port.out.JwtServicePort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.Role;
import com.bescobar.notes.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class UserUseCaseImpl implements UserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtServicePort jwtServicePort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    @Override
    @Transactional
    public AuthResponseDto registerUser(User user) {
        if (userRepositoryPort.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepositoryPort.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole() != null ? user.getRole() : Role.REGULAR);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepositoryPort.save(user);
        String accessToken = jwtServicePort.generateAccessToken(savedUser.getEmail());
        String refreshToken = refreshTokenRepositoryPort.createRefreshToken(savedUser);

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(savedUser)
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDto login(LoginCommand loginCommand) {
        User user = userRepositoryPort.findByEmail(loginCommand.getEmail());
        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!user.getActive()) {
            throw new RuntimeException("Account is inactive");
        }

        if (!passwordEncoder.matches(loginCommand.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String accessToken = jwtServicePort.generateAccessToken(user.getEmail());
        String refreshToken = refreshTokenRepositoryPort.createRefreshToken(user);

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDto refreshToken(String refreshToken) {
        String email;
        try {
            if (!jwtServicePort.isRefreshToken(refreshToken)) {
                throw new RuntimeException("Invalid token type");
            }

            email = jwtServicePort.extractEmail(refreshToken);

            if (jwtServicePort.isTokenExpired(refreshToken)) {
                throw new RuntimeException("Refresh token has expired");
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        User user = refreshTokenRepositoryPort.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!refreshTokenRepositoryPort.refreshTokenExists(refreshToken)) {
            throw new RuntimeException("Token has been revoked");
        }

        String accessToken = jwtServicePort.generateAccessToken(user.getEmail());
        String newRefreshToken = refreshTokenRepositoryPort.createRefreshToken(user);

        refreshTokenRepositoryPort.deleteRefreshToken(refreshToken);

        return AuthResponseDto.builder()
                .token(accessToken)
                .refreshToken(newRefreshToken)
                .user(user)
                .build();
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepositoryPort.deleteRefreshToken(refreshToken);
    }

    @Override
    public User getProfile(Long id) {
        User existingUser = userRepositoryPort.findById(id);
        if (existingUser == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        return existingUser;
    }

    @Override
    public User getProfileByEmail(String email) {
        User existingUser = userRepositoryPort.findByEmail(email);
        if (existingUser == null) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return existingUser;
    }

    @Override
    @Transactional
    public User updateProfile(Long id, UpdateProfileCommand updateCommand) {
        User existingUser = userRepositoryPort.findById(id);
        if (existingUser == null) {
            throw new RuntimeException("User not found with id: " + id);
        }

        // Check if email is being changed and if it's already taken
        if (!existingUser.getEmail().equals(updateCommand.getEmail()) &&
                userRepositoryPort.existsByEmail(updateCommand.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Check if username is being changed and if it's already taken
        if (!existingUser.getUsername().equals(updateCommand.getUsername()) &&
                userRepositoryPort.existsByUsername(updateCommand.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        // Fields NOT updated: password, role, active, createdAt
        existingUser.setUsername(updateCommand.getUsername());
        existingUser.setFullName(updateCommand.getFullName());
        existingUser.setEmail(updateCommand.getEmail());
        existingUser.setPhone(updateCommand.getPhone());
        existingUser.setAddress(updateCommand.getAddress());
        existingUser.setUpdatedAt(LocalDateTime.now());
        return userRepositoryPort.save(existingUser);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepositoryPort.findAll();
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepositoryPort.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepositoryPort.deleteById(id);
    }
}
