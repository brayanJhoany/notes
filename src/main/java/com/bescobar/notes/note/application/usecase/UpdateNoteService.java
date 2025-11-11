package com.bescobar.notes.note.application.usecase;

import com.bescobar.notes.note.application.port.in.UpdateNoteUseCase;
import com.bescobar.notes.note.application.port.in.command.UpdateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementing the UpdateNoteUseCase.
 * Handles the business logic for updating notes.
 * Validates ownership before allowing updates.
 */
@Service
@AllArgsConstructor
public class UpdateNoteService implements UpdateNoteUseCase {

    private final NoteRepositoryPort noteRepositoryPort;

    @Override
    public NoteDTO updateNote(UpdateNoteCommand command, Long id) {
        // 1. Verificar que la nota existe
        Note existingNote = noteRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + id));

        // 2. Verificar que el usuario es el propietario de la nota (seguridad)
        if (!existingNote.getOwner().getId().equals(command.getOwner().getId())) {
            throw new IllegalArgumentException("User is not the owner of this note");
        }

        // 4. Crear nueva instancia de Note con los datos actualizados
        Note updatedNote = new Note(
            existingNote.getId(),
            command.getTitle(),
            command.getContent(),
            command.getOwner()
        );

        // 5. Guardar la nota actualizada
        Note savedNote = noteRepositoryPort.update(updatedNote, id);

        // 6. Mapear a DTO
        return NoteDTO.builder()
            .id(savedNote.getId())
            .title(savedNote.getTitle())
            .content(savedNote.getContent())
            .createdAt(savedNote.getCreatedAt())
            .userId(savedNote.getOwner().getId())
            .build();
    }
}
