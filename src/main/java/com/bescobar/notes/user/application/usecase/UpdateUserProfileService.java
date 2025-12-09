package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.dto.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.in.UpdateProfileUseCase;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service implementing the UpdateProfileUseCase.
 * Handles updating user profile information with validation.
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class UpdateUserProfileService implements UpdateProfileUseCase {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional
    public User updateProfile(Long id, UpdateProfileCommand updateCommand) {
        User existingUser = userRepositoryPort.findById(id);
        if (existingUser == null) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        // Check if email is being changed and if it's already taken
        if (!existingUser.getEmail().equals(updateCommand.getEmail()) &&
                userRepositoryPort.existsByEmail(updateCommand.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Fields NOT updated: password, role, active, createdAt
        existingUser.setFullName(updateCommand.getFullName());
        existingUser.setEmail(updateCommand.getEmail());
        existingUser.setPhone(updateCommand.getPhone());
        existingUser.setAddress(updateCommand.getAddress());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return userRepositoryPort.save(existingUser);
    }
}
