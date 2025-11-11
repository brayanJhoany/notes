package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.application.dto.UpdateProfileCommand;
import com.bescobar.notes.user.domain.model.User;

/**
 * Use case for updating user profile information.
 */
public interface UpdateProfileUseCase {
    /**
     * Updates a user's profile information.
     *
     * @param id the user ID
     * @param updateCommand the update data
     * @return the updated user
     * @throws RuntimeException if user is not found or email/username is already taken
     */
    User updateProfile(Long id, UpdateProfileCommand updateCommand);
}
