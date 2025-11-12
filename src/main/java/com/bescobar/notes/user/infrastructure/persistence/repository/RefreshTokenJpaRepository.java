package com.bescobar.notes.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.infrastructure.persistence.entity.RefreshTokenEntity;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    @Modifying
    @Transactional
    void deleteByToken(String token);

    boolean existsByToken(String token);
}