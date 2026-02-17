package com.bescobar.notes.noteshare.domain.exception;

import com.bescobar.notes.shared.exception.EntityNotFoundException;

public class NoteNotFoundException extends EntityNotFoundException {

    public NoteNotFoundException(Long id) {
        super("Note", id);
    }

    public NoteNotFoundException(String message) {
        super(message);
    }
}
