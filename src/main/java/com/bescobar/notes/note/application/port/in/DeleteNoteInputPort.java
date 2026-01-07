package com.bescobar.notes.note.application.port.in;

public interface DeleteNoteInputPort {
    void deleteNoteById(Long id, Long ownerId);
}
