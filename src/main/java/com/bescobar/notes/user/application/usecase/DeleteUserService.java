package com.bescobar.notes.user.application.usecase;

import com.bescobar.notes.user.application.port.in.DeleteUserUseCase;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.exception.UserNotFoundException;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.repository.RefreshTokenJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementing the DeleteUserUseCase.
 * Handles user deletion from the system.
 * Ensures cascade deletion of associated refresh tokens.
 */
@Service
@AllArgsConstructor
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RefreshTokenJpaRepository refreshTokenRepository;

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepositoryPort.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        refreshTokenRepository.deleteByUserId(id);
        userRepositoryPort.deleteById(id);
    }
}
