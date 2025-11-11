package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.domain.model.User;

/**
 * Use case for retrieving user profile information.
 */
public interface GetProfileUseCase {
    /**
     * Retrieves a user profile by ID.
     *
     * @param id the user ID
     * @return the user profile
     * @throws RuntimeException if user is not found
     */
    User getProfileById(Long id);

    /**
     * Retrieves a user profile by email.
     *
     * @param email the user email
     * @return the user profile
     * @throws RuntimeException if user is not found
     */
    User getProfileByEmail(String email);
}
