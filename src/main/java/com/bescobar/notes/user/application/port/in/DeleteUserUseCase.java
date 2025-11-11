package com.bescobar.notes.user.application.port.in;

/**
 * Use case for deleting a user from the system.
 * Typically restricted to administrators.
 */
public interface DeleteUserUseCase {
    /**
     * Deletes a user by ID.
     *
     * @param id the user ID to delete
     * @throws RuntimeException if user is not found
     */
    void deleteUser(Long id);
}
