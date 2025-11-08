package com.bescobar.notes.note.application.usecase;

import com.bescobar.notes.note.application.port.in.ListUserNotesUseCase;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.domain.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementing the ListUserNotesUseCase.
 * Retrieves notes and maps them to DTOs for the application layer.
 */
@Service
@AllArgsConstructor
public class ListUserNotesService implements ListUserNotesUseCase {

    private final NoteRepositoryPort noteRepositoryPort;

    @Override
    public List<NoteDTO> findByUser(User owner) {
        List<Note> notes = noteRepositoryPort.findByUser(owner);

        return notes.stream()
            .map(note -> NoteDTO.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .userId(note.getOwner().getId())
                .build())
            .collect(Collectors.toList());
    }
}
