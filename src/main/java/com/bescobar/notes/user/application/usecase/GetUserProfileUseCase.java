package com.bescobar.notes.user.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.user.application.port.in.GetProfileInputPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.User;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;

/**
 * Service implementing the GetProfileUseCase.
 * Handles retrieval of user profile information.
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class GetUserProfileUseCase implements GetProfileInputPort {

    private final UserRepositoryOutputPort userRepositoryOutputPort;

    @Override
    public User getProfileById(Long id) {
        User existingUser = userRepositoryOutputPort.findById(id);
        if (existingUser == null) {
            throw new UserNotFoundException(id);
        }
        return existingUser;
    }

    @Override
    public User getProfileByEmail(String email) {
        User existingUser = userRepositoryOutputPort.findByEmail(email);
        if (existingUser == null) {
            throw new UserNotFoundException(email);
        }
        return existingUser;
    }
}
