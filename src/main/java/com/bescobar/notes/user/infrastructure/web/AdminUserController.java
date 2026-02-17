package com.bescobar.notes.user.infrastructure.web;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bescobar.notes.user.application.port.in.command.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.in.DeleteUserInputPort;
import com.bescobar.notes.user.application.port.in.GetProfileInputPort;
import com.bescobar.notes.user.application.port.in.ListUsersInputPort;
import com.bescobar.notes.user.application.port.in.UpdateProfileInputPort;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.shared.infrastructure.web.dto.PageResponse;
import com.bescobar.notes.user.infrastructure.web.dto.UpdateUserRequest;
import com.bescobar.notes.user.infrastructure.web.dto.UserResponse;
import com.bescobar.notes.user.infrastructure.web.mapper.UserDtoMapper;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

/**
 * AdminUserController - Administrative User Management
 *
 * Restricted to users with ADMIN role only. Allows administrators to manage all
 * users in the system.
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@AllArgsConstructor
public class AdminUserController {

    private final ListUsersInputPort listUsersUseCase;
    private final GetProfileInputPort getProfileUseCase;
    private final UpdateProfileInputPort updateProfileUseCase;
    private final DeleteUserInputPort deleteUserUseCase;

    /**
     * Get all users in the system Admin only - GET /api/admin/users
     */
    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String fullname
    ) {
        PageResponse<UserResponse> response = PageResponse.from(
                listUsersUseCase.getAllUsers(pageable, email, fullname)
                        .map(UserDtoMapper::toResponse)
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific user by ID Admin only - GET /api/admin/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = getProfileUseCase.getProfileById(id);
        UserResponse response = UserDtoMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a specific user by ID Admin only - PUT /api/admin/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        UpdateProfileCommand updateCommand = UserDtoMapper.toUpdateProfileCommand(request);
        User updatedUser = updateProfileUseCase.updateProfile(id, updateCommand);
        UserResponse response = UserDtoMapper.toResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a user by ID Admin only - DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        deleteUserUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
