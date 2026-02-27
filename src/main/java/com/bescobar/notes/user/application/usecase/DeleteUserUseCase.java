package com.bescobar.notes.user.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.application.port.in.DeleteUserInputPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.repository.RefreshTokenJpaRepository;

import lombok.AllArgsConstructor;

/**
 * Service implementing the DeleteUserUseCase.
 * Handles user deletion from the system.
 * Ensures cascade deletion of associated refresh tokens.
 */
@Service
@AllArgsConstructor
public class DeleteUserUseCase implements DeleteUserInputPort {

    private final UserRepositoryOutputPort userRepositoryOutputPort;
    private final RefreshTokenJpaRepository refreshTokenRepository;

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepositoryOutputPort.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        refreshTokenRepository.deleteByUserId(id);
        userRepositoryOutputPort.deleteById(id);
    }
}
