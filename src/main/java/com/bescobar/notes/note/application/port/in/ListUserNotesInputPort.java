package com.bescobar.notes.note.application.port.in;

import com.bescobar.notes.note.application.port.in.query.NoteDTO;
import com.bescobar.notes.user.domain.model.User;

import java.util.List;

/**
 * Use case port for listing user notes.
 * Returns DTOs instead of domain models to avoid exposing domain to outer layers.
 */
public interface ListUserNotesInputPort {
    List<NoteDTO> findByUser(User owner);
}
