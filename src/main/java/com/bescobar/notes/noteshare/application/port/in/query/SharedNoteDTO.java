package com.bescobar.notes.noteshare.application.port.in.query;

import com.bescobar.notes.noteshare.domain.model.SharePermission;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO representing a shared note with full details.
 * Includes note content and user information.
 */
@Getter
@Builder
public class SharedNoteDTO {
    private final Long id;
    private final Long noteId;
    private final String noteTitle;
    private final String noteContent;
    private final Long sharedByUserId;
    private final String sharedByUserName;
    private final Long sharedWithUserId;
    private final String sharedWithUserName;
    private final SharePermission permission;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
