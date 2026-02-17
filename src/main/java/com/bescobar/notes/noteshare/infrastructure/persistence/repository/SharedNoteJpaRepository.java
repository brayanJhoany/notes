package com.bescobar.notes.noteshare.infrastructure.persistence.repository;

import com.bescobar.notes.noteshare.infrastructure.persistence.entity.SharedNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for SharedNoteEntity.
 */
@Repository
public interface SharedNoteJpaRepository extends JpaRepository<SharedNoteEntity, Long> {

    /**
     * Finds all shared notes where the current user is the recipient.
     */
    List<SharedNoteEntity> findBySharedWithUserId(Long sharedWithUserId);

    /**
     * Finds all shared notes where the current user is the owner/sharer.
     */
    List<SharedNoteEntity> findBySharedByUserId(Long sharedByUserId);

    /**
     * Checks if a note is already shared with a specific user.
     */
    boolean existsByNoteIdAndSharedWithUserId(Long noteId, Long sharedWithUserId);

    /**
     * Finds a shared note by note ID and recipient user ID.
     */
    Optional<SharedNoteEntity> findByNoteIdAndSharedWithUserId(Long noteId, Long sharedWithUserId);
}
