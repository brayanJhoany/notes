package com.bescobar.notes.note.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.note.application.port.in.UpdateNoteUseCase;
import com.bescobar.notes.note.application.port.in.command.UpdateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;

import lombok.AllArgsConstructor;

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
        Note existingNote = noteRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + id));

        if (!existingNote.getOwner().getId().equals(command.getOwner().getId())) {
            throw new IllegalArgumentException("User is not the owner of this note");
        }

        Note updatedNote = new Note(
            existingNote.getId(),
            command.getTitle(),
            command.getContent(),
            command.getOwner()
        );

        Note savedNote = noteRepositoryPort.update(updatedNote, id);

        return NoteDTO.builder()
            .id(savedNote.getId())
            .title(savedNote.getTitle())
            .content(savedNote.getContent())
            .createdAt(savedNote.getCreatedAt())
            .userId(savedNote.getOwner().getId())
            .build();
    }
}
