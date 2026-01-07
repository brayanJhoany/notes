package com.bescobar.notes.user.application.usecase;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bescobar.notes.user.application.port.in.ListUsersInputPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Service implementing the ListUsersUseCase.
 * Handles retrieval of all users in the system.
 */
@Service
@AllArgsConstructor
public class ListUsersUseCase implements ListUsersInputPort {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public Page<User> getAllUsers(Pageable pageable, String email, String fullname) {
        return userRepositoryPort.findAll(pageable, email, fullname);
    }
}
