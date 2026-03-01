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
import static com.bescobar.notes.user.infrastructure.persistence.repository.specification.UserSpecifications.emailContains;
import static com.bescobar.notes.user.infrastructure.persistence.repository.specification.UserSpecifications.fullnameContains;
import static com.bescobar.notes.user.infrastructure.persistence.repository.specification.UserSpecifications.isActive;

import lombok.AllArgsConstructor;

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
        UserEntity userEntity = userJpaRepository.findByEmailAndActiveTrue(email);
        return userMapper.toDomain(userEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmailAndActiveTrue(email);
    }

    @Override
    public User findById(Long id) {
        UserEntity userEntity = userJpaRepository.findByIdAndActiveTrue(id);
        return userMapper.toDomain(userEntity);
    }

    @Override
    public Page<User> findAll(Pageable pageable, String email, String fullname) {
        Specification<UserEntity> spec = isActive().and(emailContains(email)).and(fullnameContains(fullname));

        Page<UserEntity> userPage = userJpaRepository.findBy(spec, q -> q.page(pageable));

        return userPage.map(userMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.findById(id).ifPresent(userEntity -> {
            userEntity.setActive(false);
            userJpaRepository.save(userEntity);
        });
    }
}
