package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.application.port.in.command.UpdateProfileCommand;
import com.bescobar.notes.user.domain.model.User;

/**
 * Use case for updating user profile information.
 */
public interface UpdateProfileInputPort {
    /**
     * Updates a user's profile information.
     *
     * @param id the user ID
     * @param updateCommand the update data
     * @return the updated user
     * @throws RuntimeException if user is not found or email is already taken
     */
    User updateProfile(Long id, UpdateProfileCommand updateCommand);
}
