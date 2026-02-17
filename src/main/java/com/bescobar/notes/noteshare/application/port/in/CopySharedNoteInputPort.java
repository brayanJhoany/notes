package com.bescobar.notes.noteshare.application.port.in;

import com.bescobar.notes.noteshare.application.port.in.command.CopySharedNoteCommand;
import com.bescobar.notes.note.domain.model.Note;

/**
 * Use case for copying a shared note (copy-on-write pattern).
 * Creates an independent copy owned by the current user.
 */
public interface CopySharedNoteInputPort {
    Note copySharedNote(Long currentUserId, CopySharedNoteCommand command);
}
