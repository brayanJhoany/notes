package com.bescobar.notes.user.application.port.in;

import com.bescobar.notes.user.domain.model.User;
import java.util.List;

/**
 * Use case for listing all users in the system.
 * Typically restricted to administrators.
 */
public interface ListUsersUseCase {
    /**
     * Retrieves all users in the system.
     *
     * @return list of all users
     */
    List<User> getAllUsers();
}
