package com.bescobar.notes.noteshare.infrastructure.persistence;

import com.bescobar.notes.noteshare.application.port.out.NoteShareRepositoryPort;
import com.bescobar.notes.noteshare.domain.model.SharedNote;
import com.bescobar.notes.noteshare.infrastructure.persistence.entity.SharedNoteEntity;
import com.bescobar.notes.noteshare.infrastructure.persistence.mapper.SharedNoteMapper;
import com.bescobar.notes.noteshare.infrastructure.persistence.repository.SharedNoteJpaRepository;
import com.bescobar.notes.note.infrastructure.persistence.entity.NoteEntity;
import com.bescobar.notes.note.infrastructure.persistence.repository.NoteJpaRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter implementing NoteShareRepositoryPort using JPA.
 */
@Repository
@AllArgsConstructor
public class NoteShareRepositoryAdapter implements NoteShareRepositoryPort {

    private final SharedNoteJpaRepository sharedNoteJpaRepository;
    private final NoteJpaRepository noteJpaRepository;
    private final SharedNoteMapper sharedNoteMapper;

    @Override
    public SharedNote save(@NotNull SharedNote sharedNote) {
        // Get the note entity
        NoteEntity noteEntity = noteJpaRepository.findById(sharedNote.getNoteId())
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + sharedNote.getNoteId()));

        // Convert to entity and save
        SharedNoteEntity entity = sharedNoteMapper.toEntity(sharedNote, noteEntity);
        SharedNoteEntity savedEntity = sharedNoteJpaRepository.save(entity);

        return sharedNoteMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<SharedNote> findById(@NotNull Long id) {
        return sharedNoteJpaRepository.findById(id)
                .map(sharedNoteMapper::toDomain);
    }

    @Override
    public List<SharedNote> findBySharedWithUserId(@NotNull Long userId) {
        return sharedNoteJpaRepository.findBySharedWithUserId(userId).stream()
                .map(sharedNoteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SharedNote> findBySharedByUserId(@NotNull Long userId) {
        return sharedNoteJpaRepository.findBySharedByUserId(userId).stream()
                .map(sharedNoteMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByNoteIdAndSharedWithUserId(@NotNull Long noteId, @NotNull Long sharedWithUserId) {
        return sharedNoteJpaRepository.existsByNoteIdAndSharedWithUserId(noteId, sharedWithUserId);
    }

    @Override
    public void deleteById(@NotNull Long id) {
        sharedNoteJpaRepository.deleteById(id);
    }

    @Override
    public Optional<SharedNote> findByNoteIdAndSharedWithUserId(@NotNull Long noteId, @NotNull Long userId) {
        return sharedNoteJpaRepository.findByNoteIdAndSharedWithUserId(noteId, userId)
                .map(sharedNoteMapper::toDomain);
    }
}
