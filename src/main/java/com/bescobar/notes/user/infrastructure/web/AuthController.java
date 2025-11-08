package com.bescobar.notes.user.infrastructure.web;

import com.bescobar.notes.user.application.dto.LoginCommand;
import com.bescobar.notes.user.application.port.in.UserUseCase;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.web.dto.AuthRequest;
import com.bescobar.notes.user.infrastructure.web.dto.AuthResponse;
import com.bescobar.notes.user.infrastructure.web.dto.RefreshTokenRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;
import com.bescobar.notes.user.infrastructure.web.dto.mapper.UserDtoMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserUseCase userUseCase;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRequest request){
        try {
            User domainUser = UserDtoMapper.toDomain(request);
            var authResult = userUseCase.registerUser(domainUser);
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

            var authResult = userUseCase.login(loginCommand);
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
            var authResult = userUseCase.refreshToken(request.getRefreshToken());
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
            userUseCase.logout(request.getRefreshToken());
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
