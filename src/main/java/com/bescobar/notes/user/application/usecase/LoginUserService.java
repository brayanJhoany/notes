package com.bescobar.notes.user.application.usecase;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.application.dto.AuthResponseDto;
import com.bescobar.notes.user.application.dto.LoginCommand;
import com.bescobar.notes.user.application.port.in.LoginUserUseCase;
import com.bescobar.notes.user.application.port.out.JwtServicePort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.exception.UserAccountDisabledException;
import com.bescobar.notes.user.domain.exception.UserAuthenticationException;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Service implementing the LoginUserUseCase. Handles user authentication with
 * email and password.
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
            throw new UserNotFoundException(loginCommand.getEmail());
        }

        if (!user.getActive()) {
            throw new UserAccountDisabledException(loginCommand.getEmail());
        }

        if (!passwordEncoder.matches(loginCommand.getPassword(), user.getPassword())) {
            throw new UserAuthenticationException("Invalid email or password");
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
