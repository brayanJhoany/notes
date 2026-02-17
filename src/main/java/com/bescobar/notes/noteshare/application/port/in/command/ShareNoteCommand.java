package com.bescobar.notes.noteshare.application.port.in.command;

import com.bescobar.notes.noteshare.domain.model.SharePermission;
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
