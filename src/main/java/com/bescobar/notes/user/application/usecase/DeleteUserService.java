package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.port.in.DeleteUserUseCase;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the DeleteUserUseCase.
 * Handles user deletion from the system.
 */
@Service
@AllArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepositoryPort.findById(id);
        if (user == null) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepositoryPort.deleteById(id);
    }
}
