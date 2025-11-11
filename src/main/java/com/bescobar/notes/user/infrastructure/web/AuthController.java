package com.bescobar.notes.user.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bescobar.notes.user.application.dto.LoginCommand;
import com.bescobar.notes.user.application.port.in.RegisterUserUseCase;
import com.bescobar.notes.user.application.port.in.LoginUserUseCase;
import com.bescobar.notes.user.application.port.in.RefreshTokenUseCase;
import com.bescobar.notes.user.application.port.in.LogoutUserUseCase;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.web.dto.AuthRequest;
import com.bescobar.notes.user.infrastructure.web.dto.AuthResponse;
import com.bescobar.notes.user.infrastructure.web.dto.RefreshTokenRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;
import com.bescobar.notes.user.infrastructure.web.dto.mapper.UserDtoMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUserUseCase logoutUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRequest request){
        try {
            User domainUser = UserDtoMapper.toDomain(request);
            var authResult = registerUserUseCase.registerUser(domainUser);
            UserResponse userResponse = UserDtoMapper.toResponse(authResult.getUser());

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(authResult.getToken())
                    .refreshToken(authResult.getRefreshToken())
                    .user(userResponse)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request){
        try {
            // Map infrastructure DTO to application command
            LoginCommand loginCommand = UserDtoMapper.toLoginCommand(request);

            var authResult = loginUserUseCase.login(loginCommand);
            UserResponse userResponse = UserDtoMapper.toResponse(authResult.getUser());

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(authResult.getToken())
                    .refreshToken(authResult.getRefreshToken())
                    .user(userResponse)
                    .build();

            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request){
        try {
            var authResult = refreshTokenUseCase.refreshToken(request.getRefreshToken());
            UserResponse userResponse = UserDtoMapper.toResponse(authResult.getUser());

            AuthResponse authResponse = AuthResponse.builder()
                    .accessToken(authResult.getToken())
                    .refreshToken(authResult.getRefreshToken())
                    .user(userResponse)
                    .build();

            return ResponseEntity.ok(authResponse);
        } catch (RuntimeException e) {
            System.err.println("Error refreshing token: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request){
        try{
            logoutUserUseCase.logout(request.getRefreshToken());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
