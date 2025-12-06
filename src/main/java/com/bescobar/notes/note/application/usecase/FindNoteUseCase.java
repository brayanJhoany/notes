package com.bescobar.notes.note.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.note.application.port.in.FindNoteInputPort;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Service implementing the FindNoteUseCase.
 * Retrieves a note by ID and maps it to a DTO.
 */
@Service
@AllArgsConstructor
public class FindNoteUseCase implements FindNoteInputPort {

    private final NoteRepositoryPort noteRepositoryPort;

    @Override
    public NoteDTO findById(User owner, Long id) {
        Note note = noteRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Note not found with id: " + id));
        
        if (!note.getOwner().getId().equals(owner.getId())) {
            throw new IllegalArgumentException("Note not associated with the user");
        }
        return NoteDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .userId(note.getOwner().getId())
                .build();
    }
}
