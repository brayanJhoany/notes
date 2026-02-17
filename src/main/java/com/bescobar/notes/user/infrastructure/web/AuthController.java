package com.bescobar.notes.user.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bescobar.notes.user.application.port.in.command.LoginCommand;
import com.bescobar.notes.user.application.port.in.LoginUserInputPort;
import com.bescobar.notes.user.application.port.in.LogoutUserInputPort;
import com.bescobar.notes.user.application.port.in.RefreshTokenInputPort;
import com.bescobar.notes.user.application.port.in.RegisterUserInputPort;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.web.dto.AuthRequest;
import com.bescobar.notes.user.infrastructure.web.dto.AuthResponse;
import com.bescobar.notes.user.infrastructure.web.dto.RefreshTokenRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;
import com.bescobar.notes.user.infrastructure.web.mapper.UserDtoMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterUserInputPort registerUserUseCase;
    private final LoginUserInputPort loginUserUseCase;
    private final RefreshTokenInputPort refreshTokenUseCase;
    private final LogoutUserInputPort logoutUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRequest request) {
        User domainUser = UserDtoMapper.toDomain(request);
        var registerUser = registerUserUseCase.registerUser(domainUser);
        UserResponse response = UserDtoMapper.toResponse(registerUser.getUser());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(registerUser.getAccessToken())
                .tokenType(registerUser.getTokenType())
                .expiresIn(registerUser.getExpiresIn())
                .refreshToken(registerUser.getRefreshToken())
                .user(response)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        LoginCommand loginCommand = UserDtoMapper.toLoginCommand(request);

        var authResult = loginUserUseCase.login(loginCommand);
        UserResponse userResponse = UserDtoMapper.toResponse(authResult.getUser());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(authResult.getAccessToken())
                .tokenType(authResult.getTokenType())
                .expiresIn(authResult.getExpiresIn())
                .refreshToken(authResult.getRefreshToken())
                .user(userResponse)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        var authResult = refreshTokenUseCase.refreshToken(request.getRefreshToken());
        UserResponse userResponse = UserDtoMapper.toResponse(authResult.getUser());

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(authResult.getAccessToken())
                .tokenType(authResult.getTokenType())
                .expiresIn(authResult.getExpiresIn())
                .refreshToken(authResult.getRefreshToken())
                .user(userResponse)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        logoutUserUseCase.logout(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
