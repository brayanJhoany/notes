package com.bescobar.notes.NoteShare.application.port.out;

import com.bescobar.notes.NoteShare.domain.model.SharedNote;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for SharedNote persistence operations.
 */
public interface NoteShareRepositoryPort {

    /**
     * Saves a new shared note.
     */
    SharedNote save(SharedNote sharedNote);

    /**
     * Finds a shared note by ID.
     */
    Optional<SharedNote> findById(Long id);

    /**
     * Finds all notes shared with a specific user.
     */
    List<SharedNote> findBySharedWithUserId(Long userId);

    /**
     * Finds all notes shared by a specific user.
     */
    List<SharedNote> findBySharedByUserId(Long userId);

    /**
     * Checks if a note is already shared with a specific user.
     */
    boolean existsByNoteIdAndSharedWithUserId(Long noteId, Long sharedWithUserId);

    /**
     * Deletes a shared note by ID.
     */
    void deleteById(Long id);

    /**
     * Finds a shared note by note ID and user ID (recipient).
     */
    Optional<SharedNote> findByNoteIdAndSharedWithUserId(Long noteId, Long userId);
}
