package com.bescobar.notes.note.application.usecase;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bescobar.notes.note.application.port.in.ListUserNotesInputPort;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.note.application.port.out.NoteRepositoryOutputPort;
import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.domain.model.User;

import lombok.AllArgsConstructor;

/**
 * Service implementing the ListUserNotesUseCase.
 * Retrieves notes and maps them to DTOs for the application layer.
 */
@Service
@AllArgsConstructor
public class ListUserNotesUseCase implements ListUserNotesInputPort {

    private final NoteRepositoryOutputPort noteRepositoryPort;

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
