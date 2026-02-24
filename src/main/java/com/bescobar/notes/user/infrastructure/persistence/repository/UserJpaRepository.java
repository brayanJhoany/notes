package com.bescobar.notes.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bescobar.notes.user.infrastructure.persistence.entity.UserEntity;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {
    boolean existsByEmail(String email);

    UserEntity findByEmail(String email);
}
