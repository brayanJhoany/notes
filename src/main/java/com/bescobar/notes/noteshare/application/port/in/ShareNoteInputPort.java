package com.bescobar.notes.noteshare.application.port.in;

import com.bescobar.notes.noteshare.application.port.in.command.ShareNoteCommand;
import com.bescobar.notes.noteshare.domain.model.SharedNote;

/**
 * Use case for sharing a note with another user.
 */
public interface ShareNoteInputPort {
    SharedNote shareNote(Long currentUserId, ShareNoteCommand command);
}
