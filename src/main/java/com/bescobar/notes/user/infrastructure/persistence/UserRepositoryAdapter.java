package com.bescobar.notes.user.infrastructure.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.model.User;
import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;
import com.bescobar.notes.user.infrastructure.persistence.mapper.UserMapper;
import com.bescobar.notes.user.infrastructure.persistence.repository.UserJpaRepository;

import lombok.AllArgsConstructor;
import static com.bescobar.notes.user.infrastructure.persistence.repository.specification.UserSpecifications.emailContains;
import static com.bescobar.notes.user.infrastructure.persistence.repository.specification.UserSpecifications.fullnameContains;

@Repository
@AllArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryOutputPort {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(userEntity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    public User findByEmail(String email) {
        UserEntity userEntity = userJpaRepository.findByEmail(email);
        return userMapper.toDomain(userEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User findById(Long id) {
        UserEntity userEntity = userJpaRepository.findById(id).orElse(null);
        return userMapper.toDomain(userEntity);
    }

    @Override
    public Page<User> findAll(Pageable pageable, String email, String fullname) {
        Specification<UserEntity> spec = Specification.where(emailContains(email))
                .and(fullnameContains(fullname));

        Page<UserEntity> userPage = userJpaRepository.findAll(spec, pageable);

        return userPage.map(userMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        // Load the entity first to ensure cascade operations work correctly
        // This allows JPA to delete associated entities (e.g., refresh tokens)
        userJpaRepository.findById(id).ifPresent(userJpaRepository::delete);
    }
}
