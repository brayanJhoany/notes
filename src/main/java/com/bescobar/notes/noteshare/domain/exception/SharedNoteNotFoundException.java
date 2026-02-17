package com.bescobar.notes.noteshare.domain.exception;

import com.bescobar.notes.shared.exception.EntityNotFoundException;

public class SharedNoteNotFoundException extends EntityNotFoundException {

    public SharedNoteNotFoundException(Long id) {
        super("SharedNote", id);
    }

    public SharedNoteNotFoundException(String message) {
        super(message);
    }
}
