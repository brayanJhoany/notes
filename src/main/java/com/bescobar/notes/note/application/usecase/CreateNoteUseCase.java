package com.bescobar.notes.note.application.usecase;

import com.bescobar.notes.note.application.port.in.CreateNoteInputPort;
import com.bescobar.notes.note.application.port.in.command.CreateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.application.port.out.NoteRepositoryOutputPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.application.port.out.UserRepositoryOutputPort;
import com.bescobar.notes.user.domain.model.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementing the CreateNoteUseCase.
 * Handles the business logic for creating notes.
 * Receives commands and returns DTOs - no infrastructure dependencies.
 */
@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class CreateNoteUseCase implements CreateNoteInputPort {

    private final NoteRepositoryOutputPort noteRepositoryPort;
    private final UserRepositoryOutputPort userRepositoryPort;

    @Override
    public NoteDTO create(CreateNoteCommand command) {
        command.validate();
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
