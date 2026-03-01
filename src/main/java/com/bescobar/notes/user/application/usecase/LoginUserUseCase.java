package com.bescobar.notes.user.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.application.port.in.LoginUserInputPort;
import com.bescobar.notes.user.application.port.in.command.LoginCommand;
import com.bescobar.notes.user.application.port.in.query.AuthResponseDto;
import com.bescobar.notes.user.application.port.out.JwtServiceOutputPort;
import com.bescobar.notes.user.application.port.out.PasswordHashingOutputPort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryOutputPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.exception.UserAccountDisabledException;
import com.bescobar.notes.user.domain.exception.UserAuthenticationException;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.User;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;

/**
 * Service implementing the LoginUserUseCase. Handles user authentication with
 * email and password.
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class LoginUserUseCase implements LoginUserInputPort {

    private final UserRepositoryOutputPort userRepositoryOutputPort;
    private final PasswordHashingOutputPort passwordHashingOutputPort;
    private final JwtServiceOutputPort jwtServiceOutputPort;
    private final RefreshTokenRepositoryOutputPort refreshTokenRepositoryOutputPort;

    @Override
    @Transactional
    public AuthResponseDto login(LoginCommand loginCommand) {
        User user = userRepositoryOutputPort.findByEmail(loginCommand.getEmail());
        if (user == null) {
            throw new UserNotFoundException(loginCommand.getEmail());
        }

        if (!user.getActive()) {
            throw new UserAccountDisabledException(loginCommand.getEmail());
        }

        if (!passwordHashingOutputPort.matches(loginCommand.getPassword(), user.getPassword())) {
            throw new UserAuthenticationException("Invalid email or password");
        }

        String accessToken = jwtServiceOutputPort.generateAccessToken(user.getEmail());
        String refreshToken = refreshTokenRepositoryOutputPort.createRefreshToken(user);
        Long expiresIn = jwtServiceOutputPort.getAccessTokenExpiration() / 1000; // Convert ms to seconds

        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .refreshToken(refreshToken)
                .user(user)
                .build();
    }
}
