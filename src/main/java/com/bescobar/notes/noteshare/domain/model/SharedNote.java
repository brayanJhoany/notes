package com.bescobar.notes.noteshare.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * SharedNote Domain Model
 *
 * Represents a note that has been shared from one user to another.
 * Implements copy-on-write pattern: shared notes are read-only by default,
 * and can be copied to create an independent editable version.
 */
@Getter
@AllArgsConstructor
@Builder
public class SharedNote {
    private final Long id;
    private final Long noteId;
    private final Long sharedByUserId;
    private final Long sharedWithUserId;
    private final SharePermission permission;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /**
     * Validates that the user is the owner of the share (shared by).
     */
    public boolean isSharedBy(Long userId) {
        return this.sharedByUserId.equals(userId);
    }

    /**
     * Validates that the user is the recipient of the share (shared with).
     */
    public boolean isSharedWith(Long userId) {
        return this.sharedWithUserId.equals(userId);
    }

    /**
     * Checks if the user has permission to copy this shared note.
     */
    public boolean canCopy() {
        return this.permission == SharePermission.CAN_COPY;
    }
}
