package com.bescobar.notes.NoteShare.application.port.in.command;

import lombok.Builder;
import lombok.Getter;

/**
 * Command to copy a shared note (copy-on-write).
 * Creates an independent copy owned by the user.
 */
@Getter
@Builder
public class CopySharedNoteCommand {
    private final Long sharedNoteId;
}
