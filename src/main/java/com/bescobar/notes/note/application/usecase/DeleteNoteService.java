package com.bescobar.notes.note.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.note.application.port.in.DeleteNoteUseCase;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DeleteNoteService implements DeleteNoteUseCase {

    private final NoteRepositoryPort noteRepositoryPort;

    @Override
    public void deleteNoteById(Long id) {
        this.noteRepositoryPort.deleteById(id);
    }
}
