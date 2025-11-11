package com.bescobar.notes.note.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.note.application.port.in.CreateNoteUseCase;
import com.bescobar.notes.note.application.port.in.command.CreateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Service implementing the CreateNoteUseCase.
 * Handles the business logic for creating notes.
 * Receives commands and returns DTOs - no infrastructure dependencies.
 */
@Service
@AllArgsConstructor
public class CreateNoteService implements CreateNoteUseCase {

    private final NoteRepositoryPort noteRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public NoteDTO create(CreateNoteCommand command) {
        User owner = userRepositoryPort.findById(command.getUserId());

        if (owner == null) {
            throw new IllegalArgumentException("User not found with id: " + command.getUserId());
        }
        Note note = new Note(
            null,
            command.getTitle(),
            command.getContent(),
            owner
        );
        Note savedNote = noteRepositoryPort.save(note);
        return NoteDTO.builder()
            .id(savedNote.getId())
            .title(savedNote.getTitle())
            .content(savedNote.getContent())
            .createdAt(savedNote.getCreatedAt())
            .userId(savedNote.getOwner().getId())
            .build();
    }
}
