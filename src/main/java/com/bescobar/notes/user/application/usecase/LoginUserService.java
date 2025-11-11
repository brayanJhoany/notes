package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.application.dto.LoginCommand;
import com.bescobar.notes.user.application.port.in.LoginUserUseCase;
import com.bescobar.notes.user.application.port.out.JwtServicePort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the LoginUserUseCase.
 * Handles user authentication with email and password.
 */
@Service
@AllArgsConstructor
public class LoginUserService implements LoginUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtServicePort jwtServicePort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

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
}
