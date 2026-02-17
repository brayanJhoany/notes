package com.bescobar.notes.NoteShare.infrastructure.persistence.mapper;

import com.bescobar.notes.NoteShare.domain.model.SharedNote;
import com.bescobar.notes.NoteShare.infrastructure.persistence.entity.SharedNoteEntity;
import com.bescobar.notes.note.infrastructure.persistence.entity.NoteEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

/**
 * Mapper between SharedNote domain model and SharedNoteEntity JPA entity.
 */
@Component
public class SharedNoteMapper {

    /**
     * Converts domain model to JPA entity.
     * Note: The NoteEntity must be set separately after creation.
     */
    public SharedNoteEntity toEntity(@NotNull SharedNote sharedNote, NoteEntity noteEntity) {
        return SharedNoteEntity.builder()
                .id(sharedNote.getId())
                .note(noteEntity)
                .sharedByUserId(sharedNote.getSharedByUserId())
                .sharedWithUserId(sharedNote.getSharedWithUserId())
                .permission(sharedNote.getPermission())
                .createdAt(sharedNote.getCreatedAt())
                .updatedAt(sharedNote.getUpdatedAt())
                .build();
    }

    /**
     * Converts JPA entity to domain model.
     */
    public SharedNote toDomain(@NotNull SharedNoteEntity entity) {
        return SharedNote.builder()
                .id(entity.getId())
                .noteId(entity.getNote().getId())
                .sharedByUserId(entity.getSharedByUserId())
                .sharedWithUserId(entity.getSharedWithUserId())
                .permission(entity.getPermission())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
