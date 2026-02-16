package com.bescobar.notes.user.infrastructure.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bescobar.notes.user.application.dto.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.in.GetProfileInputPort;
import com.bescobar.notes.user.application.port.in.UpdateProfileInputPort;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.web.dto.UpdateUserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;
import com.bescobar.notes.user.infrastructure.web.dto.mapper.UserDtoMapper;

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

    private final GetProfileInputPort getProfileUseCase;
    private final UpdateProfileInputPort updateProfileUseCase;

    /**
     * Get current user's profile
     * Uses the email from the JWT token to retrieve the user
     */
    @GetMapping
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = getProfileUseCase.getProfileByEmail(email);
        UserResponse response = UserDtoMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Update current user's profile
     * Automatically uses the authenticated user's ID from JWT
     */
    @PutMapping
    public ResponseEntity<UserResponse> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserRequest request) {
            String email = userDetails.getUsername();
            User currentUser = getProfileUseCase.getProfileByEmail(email);

            UpdateProfileCommand updateCommand = UserDtoMapper.toUpdateProfileCommand(request);
            User updatedUser = updateProfileUseCase.updateProfile(currentUser.getId(), updateCommand);

            UserResponse response = UserDtoMapper.toResponse(updatedUser);
            return ResponseEntity.ok(response);
    }
}
