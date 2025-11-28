package com.bescobar.notes.note.application.port.in;

import com.bescobar.notes.note.application.port.in.command.CreateNoteCommand;
import com.bescobar.notes.note.application.port.in.query.NoteDTO;

/**
 * Use case port for creating notes.
 * Follows the Command pattern - receives a command and returns a DTO.
 * This is the application layer's input contract.
 */
public interface CreateNoteInputPort {
    NoteDTO create(CreateNoteCommand command);
}
