package com.bescobar.notes.user.infrastructure.web;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bescobar.notes.user.application.dto.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.in.DeleteUserUseCase;
import com.bescobar.notes.user.application.port.in.GetProfileUseCase;
import com.bescobar.notes.user.application.port.in.ListUsersUseCase;
import com.bescobar.notes.user.application.port.in.UpdateProfileUseCase;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.web.dto.UpdateUserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;
import com.bescobar.notes.user.infrastructure.web.dto.mapper.UserDtoMapper;

import jakarta.validation.Valid;

/**
 * AdminUserController - Administrative User Management
 *
 * Restricted to users with ADMIN role only.
 * Allows administrators to manage all users in the system.
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final ListUsersUseCase listUsersUseCase;
    private final GetProfileUseCase getProfileUseCase;
    private final UpdateProfileUseCase updateProfileUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public AdminUserController(
            ListUsersUseCase listUsersUseCase,
            GetProfileUseCase getProfileUseCase,
            UpdateProfileUseCase updateProfileUseCase,
            DeleteUserUseCase deleteUserUseCase) {
        this.listUsersUseCase = Objects.requireNonNull(listUsersUseCase, "listUsersUseCase is required");
        this.getProfileUseCase = Objects.requireNonNull(getProfileUseCase, "getProfileUseCase is required");
        this.updateProfileUseCase = Objects.requireNonNull(updateProfileUseCase, "updateProfileUseCase is required");
        DeleteUserUseCase safeDeleteUserUseCase = Objects.requireNonNull(deleteUserUseCase, "deleteUserUseCase is required");
        this.deleteUserUseCase = id -> safeDeleteUserUseCase.deleteUser(id);
    }

    /**
     * Get all users in the system
     * Admin only - GET /api/admin/users
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            List<User> users = listUsersUseCase.getAllUsers();
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
            User user = getProfileUseCase.getProfileById(id);
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
            User updatedUser = updateProfileUseCase.updateProfile(id, updateCommand);
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
            deleteUserUseCase.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
