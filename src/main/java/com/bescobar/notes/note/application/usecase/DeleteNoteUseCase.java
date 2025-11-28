package com.bescobar.notes.note.application.usecase;

import org.springframework.stereotype.Service;

import com.bescobar.notes.note.application.port.in.DeleteNoteInputPort;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DeleteNoteUseCase implements DeleteNoteInputPort {

    private final NoteRepositoryPort noteRepositoryPort;

    @Override
    public void deleteNoteById(Long id) {
        this.noteRepositoryPort.deleteById(id);
    }
}
