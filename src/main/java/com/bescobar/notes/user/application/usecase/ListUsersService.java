package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.port.in.ListUsersUseCase;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service implementing the ListUsersUseCase.
 * Handles retrieval of all users in the system.
 */
@Service
@AllArgsConstructor
public class ListUsersService implements ListUsersUseCase {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Page<User> getAllUsers(Pageable pageable, String email, String fullname) {
        return userRepositoryPort.findAll(pageable, email, fullname);
    }
}
