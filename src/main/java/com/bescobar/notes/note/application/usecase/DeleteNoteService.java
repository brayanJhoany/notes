package com.bescobar.notes.note.application.usecase;

import com.bescobar.notes.note.application.port.in.DeleteNoteUseCase;
import com.bescobar.notes.note.application.port.out.NoteRepositoryPort;
import com.bescobar.notes.user.application.port.out.UserRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeleteNoteService implements DeleteNoteUseCase {

    private final NoteRepositoryPort noteRepositoryPort;

    @Override
    public void deleteNoteById(Long id) {
        this.noteRepositoryPort.deleteById(id);
    }
}
