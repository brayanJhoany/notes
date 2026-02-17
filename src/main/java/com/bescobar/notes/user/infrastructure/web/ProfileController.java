package com.bescobar.notes.user.infrastructure.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bescobar.notes.shared.security.AuthenticatedUser;
import com.bescobar.notes.user.application.port.in.command.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.in.UpdateProfileInputPort;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.web.dto.UpdateUserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;
import com.bescobar.notes.user.infrastructure.web.mapper.UserDtoMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * ProfileController - Self-Service Profile Management
 *
 * Allows authenticated users to manage their own profile.
 * The user ID is extracted from the JWT token automatically,
 * ensuring users can only access/modify their own data.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/profile")
public class ProfileController {

    private final UpdateProfileInputPort updateProfileUseCase;

    /**
     * Get current user's profile
     * The authenticated user is automatically injected via @AuthenticatedUser
     */
    @GetMapping
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticatedUser User currentUser) {
        UserResponse response = UserDtoMapper.toResponse(currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Update current user's profile
     * The authenticated user is automatically injected via @AuthenticatedUser
     */
    @PutMapping
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticatedUser User currentUser,
            @Valid @RequestBody UpdateUserRequest request) {

            UpdateProfileCommand updateCommand = UserDtoMapper.toUpdateProfileCommand(request);
            User updatedUser = updateProfileUseCase.updateProfile(currentUser.getId(), updateCommand);

            UserResponse response = UserDtoMapper.toResponse(updatedUser);
            return ResponseEntity.ok(response);
    }
}
