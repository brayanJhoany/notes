package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.port.in.GetProfileInputPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementing the GetProfileUseCase.
 * Handles retrieval of user profile information.
 */
@Service
@AllArgsConstructor
public class GetUserProfileService implements GetProfileInputPort {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    public User getProfileById(Long id) {
        User existingUser = userRepositoryPort.findById(id);
        if (existingUser == null) {
            throw new UserNotFoundException(id);
        }
        return existingUser;
    }

    @Override
    public User getProfileByEmail(String email) {
        User existingUser = userRepositoryPort.findByEmail(email);
        if (existingUser == null) {
            throw new UserNotFoundException(email);
        }
        return existingUser;
    }
}
