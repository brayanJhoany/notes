package com.bescobar.notes.NoteShare.application.port.in;

import com.bescobar.notes.NoteShare.application.port.in.command.ShareNoteCommand;
import com.bescobar.notes.NoteShare.domain.model.SharedNote;

/**
 * Use case for sharing a note with another user.
 */
public interface ShareNoteInputPort {
    SharedNote shareNote(Long currentUserId, ShareNoteCommand command);
}
