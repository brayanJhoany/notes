package com.bescobar.notes.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bescobar.notes.user.infrastructure.persistence.entity.RefreshTokenEntity;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    void deleteByToken(String token);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Transactional
    void deleteByUserId(Long userId);

    boolean existsByToken(String token);
}
