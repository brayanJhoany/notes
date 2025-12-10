package com.bescobar.notes.user.application.port.in;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bescobar.notes.user.domain.model.User;

/**
 * Use case for listing all users in the system.
 * Typically restricted to administrators.
 */
public interface ListUsersUseCase {
    /**
     * Retrieves all users in the system with pagination and filters.
     *
     * @return page of users
     */
    Page<User> getAllUsers(Pageable pageable, String email, String fullname);
}
