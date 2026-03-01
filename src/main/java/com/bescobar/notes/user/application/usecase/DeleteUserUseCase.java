package com.bescobar.notes.user.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.application.port.in.DeleteUserInputPort;
import com.bescobar.notes.user.application.port.out.RefreshTokenRepositoryOutputPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.User;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;

/**
 * Service implementing the DeleteUserUseCase.
 * Handles user deletion from the system.
 * Ensures cascade deletion of associated refresh tokens.
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class DeleteUserUseCase implements DeleteUserInputPort {

    private final UserRepositoryOutputPort userRepositoryOutputPort;
    private final RefreshTokenRepositoryOutputPort refreshTokenRepositoryOutputPort;

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepositoryOutputPort.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        refreshTokenRepositoryOutputPort.deleteAllRefreshTokensByUserId(id);
        userRepositoryOutputPort.deleteById(id);
    }
}
