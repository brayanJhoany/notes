package com.bescobar.notes.user.application.usecase;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.application.port.in.command.UpdateProfileCommand;
import com.bescobar.notes.user.application.port.in.UpdateProfileInputPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.exception.UserAlreadyExistsException;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.User;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;

/**
 * Service implementing the UpdateProfileUseCase.
 * Handles updating user profile information with validation.
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class UpdateUserProfileUseCase implements UpdateProfileInputPort {

    private final UserRepositoryOutputPort userRepositoryPort;

    @Override
    @Transactional
    public User updateProfile(Long id, UpdateProfileCommand updateCommand) {
        User userExists = userRepositoryPort.findById(id);
        if (userExists == null) {
            throw new UserNotFoundException(id);
        }

        if (!userExists.getEmail().equals(updateCommand.getEmail()) &&
                userRepositoryPort.existsByEmail(updateCommand.getEmail())) {
            throw new UserAlreadyExistsException(updateCommand.getEmail());
        }

        userExists.setFullName(updateCommand.getFullName());
        userExists.setEmail(updateCommand.getEmail());
        userExists.setPhone(updateCommand.getPhone());
        userExists.setAddress(updateCommand.getAddress());
        userExists.setUpdatedAt(LocalDateTime.now());

        return userRepositoryPort.save(userExists);
    }
}
