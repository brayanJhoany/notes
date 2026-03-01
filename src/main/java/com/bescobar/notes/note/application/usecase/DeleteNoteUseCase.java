package com.bescobar.notes.note.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.note.application.port.in.DeleteNoteInputPort;
import com.bescobar.notes.note.application.port.out.NoteRepositoryOutputPort;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring-managed dependency injection")
public class DeleteNoteUseCase implements DeleteNoteInputPort {

    private final NoteRepositoryOutputPort noteRepositoryPort;

    @Override
    public void deleteNoteById(Long id, Long ownerId) {
        var note = noteRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + id));
        if (!note.getOwner().getId().equals(ownerId)) {
            throw new IllegalArgumentException("User is not the owner of this note");
        }
        noteRepositoryPort.deleteById(id);
    }
}
