package com.bescobar.notes.NoteShare.application.port.in.command;

import com.bescobar.notes.NoteShare.domain.model.SharePermission;
import lombok.Builder;
import lombok.Getter;

/**
 * Command to share a note with another user.
 */
@Getter
@Builder
public class ShareNoteCommand {
    private final Long noteId;
    private final Long sharedWithUserId;
    private final SharePermission permission;
}
