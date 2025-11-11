package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.port.in.ListUsersUseCase;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementing the ListUsersUseCase.
 * Handles retrieval of all users in the system.
 */
@Service
@AllArgsConstructor
public class ListUsersService implements ListUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    public List<User> getAllUsers() {
        return userRepositoryPort.findAll();
    }
}
