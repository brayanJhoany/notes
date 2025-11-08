package com.bescobar.notes.user.infrastructure.web;

import com.bescobar.notes.user.application.dto.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.in.UserUseCase;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.web.dto.UpdateUserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;
import com.bescobar.notes.user.infrastructure.web.dto.mapper.UserDtoMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AdminUserController - Administrative User Management
 *
 * Restricted to users with ADMIN role only.
 * Allows administrators to manage all users in the system.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserUseCase userUseCase;

    /**
     * Get all users in the system
     * Admin only - GET /api/admin/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            List<User> users = userUseCase.getAllUsers();
            List<UserResponse> responses = users.stream()
                    .map(UserDtoMapper::toResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific user by ID
     * Admin only - GET /api/admin/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        try {
            User user = userUseCase.getProfile(id);
            UserResponse response = UserDtoMapper.toResponse(user);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Update a specific user by ID
     * Admin only - PUT /api/admin/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            UpdateProfileCommand updateCommand = UserDtoMapper.toUpdateProfileCommand(request);
            User updatedUser = userUseCase.updateProfile(id, updateCommand);
            UserResponse response = UserDtoMapper.toResponse(updatedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Delete a user by ID
     * Admin only - DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            userUseCase.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
