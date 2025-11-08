package com.bescobar.notes.note.application.port.out;

import com.bescobar.notes.note.domain.model.Note;
import com.bescobar.notes.user.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface NoteRepositoryPort {
    Note save(Note note);
    List<Note> findByUser(User user);
    Optional<Note> findById(Long id);
    void deleteById(Long id);
    Note update(Note note, Long id);
}
